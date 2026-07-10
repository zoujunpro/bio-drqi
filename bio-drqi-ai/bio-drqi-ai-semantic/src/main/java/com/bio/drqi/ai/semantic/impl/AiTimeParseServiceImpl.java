package com.bio.drqi.ai.semantic.impl;

import com.bio.drqi.ai.dto.semantic.AiTimeParseReqDTO;
import com.bio.drqi.ai.dto.semantic.AiTimeParseRspDTO;
import com.bio.drqi.ai.semantic.AiTimeParseService;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 默认时间解析实现。
 *
 * <p>这里优先覆盖企业系统里高确定性的时间表达，例如今天、上周、最近30天、
 * 2026年7月、2026-07-10、2026年1月到2026年6月等。</p>
 *
 * <p>不在这里强行解析依赖业务事件的表达，例如“春播后两周”“项目开始后三个月”。
 * 这类表达需要先查到业务事件日期，再由 Planner 或 LLM 做二次解析。</p>
 */
@Service
public class AiTimeParseServiceImpl implements AiTimeParseService {

    private static final Pattern RECENT_DAYS_PATTERN = Pattern.compile("最近(\\d+)天|近(\\d+)天");

    private static final Pattern RECENT_WEEKS_PATTERN = Pattern.compile("最近(\\d+)周|近(\\d+)周|最近(\\d+)个?星期|近(\\d+)个?星期");

    private static final Pattern RECENT_MONTHS_PATTERN = Pattern.compile("最近(\\d+)个?月|近(\\d+)个?月");

    private static final Pattern RECENT_YEARS_PATTERN = Pattern.compile("最近(\\d+)年|近(\\d+)年");

    private static final Pattern ISO_DATE_RANGE_PATTERN = Pattern.compile("(\\d{4}[-/]\\d{1,2}[-/]\\d{1,2})\\s*(?:到|至|~|-)\\s*(\\d{4}[-/]\\d{1,2}[-/]\\d{1,2})");

    private static final Pattern CN_DATE_RANGE_PATTERN = Pattern.compile("(\\d{4})年(\\d{1,2})月(\\d{1,2})日?\\s*(?:到|至|~|-)\\s*(?:(\\d{4})年)?(\\d{1,2})月(\\d{1,2})日?");

    private static final Pattern YEAR_MONTH_RANGE_PATTERN = Pattern.compile("(?:(\\d{4})年)?(\\d{1,2})月\\s*(?:到|至|~|-)\\s*(?:(\\d{4})年)?(\\d{1,2})月");

    private static final Pattern ISO_DATE_PATTERN = Pattern.compile("\\b(\\d{4})[-/](\\d{1,2})[-/](\\d{1,2})\\b");

    private static final Pattern CN_DATE_PATTERN = Pattern.compile("(\\d{4})年(\\d{1,2})月(\\d{1,2})日?");

    private static final Pattern YEAR_MONTH_PATTERN = Pattern.compile("(\\d{4})年(\\d{1,2})月");

    private static final Pattern YEAR_PATTERN = Pattern.compile("(\\d{4})年");

    private static final Pattern QUARTER_PATTERN = Pattern.compile("(?:(\\d{4})年)?(?:第?([1-4一二三四])季度|Q([1-4]))");

    private static final String DATE_PATTERN = "yyyy-MM-dd";

    @Override
    public AiTimeParseRspDTO parse(AiTimeParseReqDTO reqDTO) {
        AiTimeParseRspDTO rspDTO = new AiTimeParseRspDTO();
        rspDTO.setMatched(Boolean.FALSE);
        if (reqDTO == null || reqDTO.getQuery() == null) {
            return rspDTO;
        }

        String query = reqDTO.getQuery();
        Date referenceDate = parseReferenceDate(reqDTO.getReferenceDate());

        if (matchExplicitRange(query, referenceDate, rspDTO)) {
            return rspDTO;
        }
        if (matchExplicitDate(query, referenceDate, rspDTO)) {
            return rspDTO;
        }
        if (matchFixedRange(query, referenceDate, rspDTO)) {
            return rspDTO;
        }
        if (matchRecentDays(query, referenceDate, rspDTO)) {
            return rspDTO;
        }
        if (matchRecentWeeks(query, referenceDate, rspDTO)) {
            return rspDTO;
        }
        if (matchRecentMonths(query, referenceDate, rspDTO)) {
            return rspDTO;
        }
        if (matchRecentYears(query, referenceDate, rspDTO)) {
            return rspDTO;
        }
        return rspDTO;
    }

    private boolean matchFixedRange(String query, Date referenceDate, AiTimeParseRspDTO rspDTO) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(referenceDate);
        if (query.contains("今天")) {
            fill(rspDTO, "今天", referenceDate, referenceDate);
            return true;
        }
        if (query.contains("昨天")) {
            calendar.add(Calendar.DATE, -1);
            fill(rspDTO, "昨天", calendar.getTime(), calendar.getTime());
            return true;
        }
        if (query.contains("明天")) {
            calendar.add(Calendar.DATE, 1);
            fill(rspDTO, "明天", calendar.getTime(), calendar.getTime());
            return true;
        }
        if (query.contains("本周")) {
            fill(rspDTO, "本周", firstDayOfWeek(referenceDate, 0), lastDayOfWeek(referenceDate, 0));
            return true;
        }
        if (query.contains("上周")) {
            fill(rspDTO, "上周", firstDayOfWeek(referenceDate, -1), lastDayOfWeek(referenceDate, -1));
            return true;
        }
        if (query.contains("下周")) {
            fill(rspDTO, "下周", firstDayOfWeek(referenceDate, 1), lastDayOfWeek(referenceDate, 1));
            return true;
        }
        if (query.contains("本月")) {
            fill(rspDTO, "本月", firstDayOfMonth(referenceDate, 0), lastDayOfMonth(referenceDate, 0));
            return true;
        }
        if (query.contains("上月") || query.contains("上个月")) {
            fill(rspDTO, "上月", firstDayOfMonth(referenceDate, -1), lastDayOfMonth(referenceDate, -1));
            return true;
        }
        if (query.contains("下月") || query.contains("下个月")) {
            fill(rspDTO, "下月", firstDayOfMonth(referenceDate, 1), lastDayOfMonth(referenceDate, 1));
            return true;
        }
        if (query.contains("本季度")) {
            fill(rspDTO, "本季度", firstDayOfQuarter(referenceDate, 0), lastDayOfQuarter(referenceDate, 0));
            return true;
        }
        if (query.contains("上季度") || query.contains("上一季度")) {
            fill(rspDTO, "上季度", firstDayOfQuarter(referenceDate, -1), lastDayOfQuarter(referenceDate, -1));
            return true;
        }
        if (query.contains("下季度") || query.contains("下一季度")) {
            fill(rspDTO, "下季度", firstDayOfQuarter(referenceDate, 1), lastDayOfQuarter(referenceDate, 1));
            return true;
        }
        if (query.contains("上半年")) {
            fill(rspDTO, "上半年", firstDayOfHalfYear(referenceDate, 1), lastDayOfHalfYear(referenceDate, 1));
            return true;
        }
        if (query.contains("下半年")) {
            fill(rspDTO, "下半年", firstDayOfHalfYear(referenceDate, 2), lastDayOfHalfYear(referenceDate, 2));
            return true;
        }
        if (query.contains("今年") || query.contains("本年") || query.contains("本年度")) {
            fill(rspDTO, "今年", firstDayOfYear(referenceDate, 0), lastDayOfYear(referenceDate, 0));
            return true;
        }
        if (query.contains("去年") || query.contains("上年") || query.contains("上一年") || query.contains("上年度")) {
            fill(rspDTO, "去年", firstDayOfYear(referenceDate, -1), lastDayOfYear(referenceDate, -1));
            return true;
        }
        if (query.contains("明年") || query.contains("下年") || query.contains("下一年") || query.contains("下年度")) {
            fill(rspDTO, "明年", firstDayOfYear(referenceDate, 1), lastDayOfYear(referenceDate, 1));
            return true;
        }
        if (query.contains("近半年") || query.contains("最近半年")) {
            Calendar start = Calendar.getInstance();
            start.setTime(referenceDate);
            start.add(Calendar.MONTH, -6);
            start.add(Calendar.DATE, 1);
            fill(rspDTO, "近半年", start.getTime(), referenceDate);
            return true;
        }
        return false;
    }

    private boolean matchExplicitRange(String query, Date referenceDate, AiTimeParseRspDTO rspDTO) {
        Matcher isoMatcher = ISO_DATE_RANGE_PATTERN.matcher(query);
        if (isoMatcher.find()) {
            Date startDate = parseDate(isoMatcher.group(1).replace('/', '-'));
            Date endDate = parseDate(isoMatcher.group(2).replace('/', '-'));
            if (startDate != null && endDate != null) {
                fillOrdered(rspDTO, isoMatcher.group(), startDate, endDate);
                return true;
            }
        }

        Matcher cnMatcher = CN_DATE_RANGE_PATTERN.matcher(query);
        if (cnMatcher.find()) {
            int startYear = Integer.parseInt(cnMatcher.group(1));
            int endYear = cnMatcher.group(4) == null ? startYear : Integer.parseInt(cnMatcher.group(4));
            Date startDate = date(startYear, Integer.parseInt(cnMatcher.group(2)), Integer.parseInt(cnMatcher.group(3)));
            Date endDate = date(endYear, Integer.parseInt(cnMatcher.group(5)), Integer.parseInt(cnMatcher.group(6)));
            if (startDate != null && endDate != null) {
                fillOrdered(rspDTO, cnMatcher.group(), startDate, endDate);
                return true;
            }
        }

        Matcher monthRangeMatcher = YEAR_MONTH_RANGE_PATTERN.matcher(query);
        if (monthRangeMatcher.find()) {
            int referenceYear = year(referenceDate);
            int startYear = monthRangeMatcher.group(1) == null ? referenceYear : Integer.parseInt(monthRangeMatcher.group(1));
            int endYear = monthRangeMatcher.group(3) == null ? startYear : Integer.parseInt(monthRangeMatcher.group(3));
            Date startDate = firstDayOfMonth(startYear, Integer.parseInt(monthRangeMatcher.group(2)));
            Date endDate = lastDayOfMonth(endYear, Integer.parseInt(monthRangeMatcher.group(4)));
            fillOrdered(rspDTO, monthRangeMatcher.group(), startDate, endDate);
            return true;
        }
        return false;
    }

    private boolean matchExplicitDate(String query, Date referenceDate, AiTimeParseRspDTO rspDTO) {
        Matcher isoMatcher = ISO_DATE_PATTERN.matcher(query);
        if (isoMatcher.find()) {
            Date date = date(Integer.parseInt(isoMatcher.group(1)), Integer.parseInt(isoMatcher.group(2)), Integer.parseInt(isoMatcher.group(3)));
            if (date != null) {
                fill(rspDTO, isoMatcher.group(), date, date);
                return true;
            }
        }

        Matcher cnDateMatcher = CN_DATE_PATTERN.matcher(query);
        if (cnDateMatcher.find()) {
            Date date = date(Integer.parseInt(cnDateMatcher.group(1)), Integer.parseInt(cnDateMatcher.group(2)), Integer.parseInt(cnDateMatcher.group(3)));
            if (date != null) {
                fill(rspDTO, cnDateMatcher.group(), date, date);
                return true;
            }
        }

        Matcher quarterMatcher = QUARTER_PATTERN.matcher(query);
        if (quarterMatcher.find()) {
            int quarter = parseQuarter(quarterMatcher.group(2), quarterMatcher.group(3));
            int quarterYear = quarterMatcher.group(1) == null ? year(referenceDate) : Integer.parseInt(quarterMatcher.group(1));
            fill(rspDTO, quarterMatcher.group(), firstDayOfQuarter(quarterYear, quarter), lastDayOfQuarter(quarterYear, quarter));
            return true;
        }

        Matcher yearMonthMatcher = YEAR_MONTH_PATTERN.matcher(query);
        if (yearMonthMatcher.find()) {
            int matchYear = Integer.parseInt(yearMonthMatcher.group(1));
            int matchMonth = Integer.parseInt(yearMonthMatcher.group(2));
            fill(rspDTO, yearMonthMatcher.group(), firstDayOfMonth(matchYear, matchMonth), lastDayOfMonth(matchYear, matchMonth));
            return true;
        }

        Matcher yearMatcher = YEAR_PATTERN.matcher(query);
        if (yearMatcher.find()) {
            int matchYear = Integer.parseInt(yearMatcher.group(1));
            fill(rspDTO, yearMatcher.group(), firstDayOfYear(matchYear), lastDayOfYear(matchYear));
            return true;
        }
        return false;
    }

    private boolean matchRecentDays(String query, Date referenceDate, AiTimeParseRspDTO rspDTO) {
        Matcher matcher = RECENT_DAYS_PATTERN.matcher(query);
        if (!matcher.find()) {
            return false;
        }
        int days = parseFirstNumber(matcher);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(referenceDate);
        calendar.add(Calendar.DATE, -Math.max(days - 1, 0));
        fill(rspDTO, matcher.group(), calendar.getTime(), referenceDate);
        return true;
    }

    private boolean matchRecentMonths(String query, Date referenceDate, AiTimeParseRspDTO rspDTO) {
        Matcher matcher = RECENT_MONTHS_PATTERN.matcher(query);
        if (!matcher.find()) {
            return false;
        }
        int months = parseFirstNumber(matcher);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(referenceDate);
        calendar.add(Calendar.MONTH, -Math.max(months, 0));
        calendar.add(Calendar.DATE, 1);
        fill(rspDTO, matcher.group(), calendar.getTime(), referenceDate);
        return true;
    }

    private boolean matchRecentWeeks(String query, Date referenceDate, AiTimeParseRspDTO rspDTO) {
        Matcher matcher = RECENT_WEEKS_PATTERN.matcher(query);
        if (!matcher.find()) {
            return false;
        }
        int weeks = parseFirstNumber(matcher);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(referenceDate);
        calendar.add(Calendar.DATE, -Math.max(weeks * 7 - 1, 0));
        fill(rspDTO, matcher.group(), calendar.getTime(), referenceDate);
        return true;
    }

    private boolean matchRecentYears(String query, Date referenceDate, AiTimeParseRspDTO rspDTO) {
        Matcher matcher = RECENT_YEARS_PATTERN.matcher(query);
        if (!matcher.find()) {
            return false;
        }
        int years = parseFirstNumber(matcher);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(referenceDate);
        calendar.add(Calendar.YEAR, -Math.max(years, 0));
        calendar.add(Calendar.DATE, 1);
        fill(rspDTO, matcher.group(), calendar.getTime(), referenceDate);
        return true;
    }

    private int parseFirstNumber(Matcher matcher) {
        for (int i = 1; i <= matcher.groupCount(); i++) {
            if (matcher.group(i) != null) {
                return Integer.parseInt(matcher.group(i));
            }
        }
        return 1;
    }

    private Date firstDayOfWeek(Date referenceDate, int weekOffset) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(referenceDate);
        calendar.add(Calendar.WEEK_OF_YEAR, weekOffset);
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        return calendar.getTime();
    }

    private Date lastDayOfWeek(Date referenceDate, int weekOffset) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(firstDayOfWeek(referenceDate, weekOffset));
        calendar.add(Calendar.DATE, 6);
        return calendar.getTime();
    }

    private Date firstDayOfMonth(Date referenceDate, int monthOffset) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(referenceDate);
        calendar.add(Calendar.MONTH, monthOffset);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        return calendar.getTime();
    }

    private Date lastDayOfMonth(Date referenceDate, int monthOffset) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(firstDayOfMonth(referenceDate, monthOffset));
        calendar.add(Calendar.MONTH, 1);
        calendar.add(Calendar.DATE, -1);
        return calendar.getTime();
    }

    private Date firstDayOfMonth(int year, int month) {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month - 1);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        return calendar.getTime();
    }

    private Date lastDayOfMonth(int year, int month) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(firstDayOfMonth(year, month));
        calendar.add(Calendar.MONTH, 1);
        calendar.add(Calendar.DATE, -1);
        return calendar.getTime();
    }

    private Date firstDayOfYear(Date referenceDate, int yearOffset) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(referenceDate);
        calendar.add(Calendar.YEAR, yearOffset);
        calendar.set(Calendar.DAY_OF_YEAR, 1);
        return calendar.getTime();
    }

    private Date lastDayOfYear(Date referenceDate, int yearOffset) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(firstDayOfYear(referenceDate, yearOffset));
        calendar.add(Calendar.YEAR, 1);
        calendar.add(Calendar.DATE, -1);
        return calendar.getTime();
    }

    private Date firstDayOfYear(int year) {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.DAY_OF_YEAR, 1);
        return calendar.getTime();
    }

    private Date lastDayOfYear(int year) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(firstDayOfYear(year));
        calendar.add(Calendar.YEAR, 1);
        calendar.add(Calendar.DATE, -1);
        return calendar.getTime();
    }

    private Date firstDayOfQuarter(Date referenceDate, int quarterOffset) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(referenceDate);
        int currentQuarter = calendar.get(Calendar.MONTH) / 3 + 1;
        int totalQuarter = currentQuarter + quarterOffset;
        int yearOffset = Math.floorDiv(totalQuarter - 1, 4);
        int quarter = Math.floorMod(totalQuarter - 1, 4) + 1;
        calendar.add(Calendar.YEAR, yearOffset);
        return firstDayOfQuarter(calendar.get(Calendar.YEAR), quarter);
    }

    private Date lastDayOfQuarter(Date referenceDate, int quarterOffset) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(firstDayOfQuarter(referenceDate, quarterOffset));
        calendar.add(Calendar.MONTH, 3);
        calendar.add(Calendar.DATE, -1);
        return calendar.getTime();
    }

    private Date firstDayOfQuarter(int year, int quarter) {
        return firstDayOfMonth(year, (quarter - 1) * 3 + 1);
    }

    private Date lastDayOfQuarter(int year, int quarter) {
        return lastDayOfMonth(year, quarter * 3);
    }

    private Date firstDayOfHalfYear(Date referenceDate, int halfYear) {
        return firstDayOfMonth(year(referenceDate), halfYear == 1 ? 1 : 7);
    }

    private Date lastDayOfHalfYear(Date referenceDate, int halfYear) {
        return lastDayOfMonth(year(referenceDate), halfYear == 1 ? 6 : 12);
    }

    private void fill(AiTimeParseRspDTO rspDTO, String expression, Date startDate, Date endDate) {
        rspDTO.setMatched(Boolean.TRUE);
        rspDTO.setExpression(expression);
        rspDTO.setStartDate(format(startDate));
        rspDTO.setEndDate(format(endDate));
    }

    private void fillOrdered(AiTimeParseRspDTO rspDTO, String expression, Date startDate, Date endDate) {
        if (startDate.after(endDate)) {
            fill(rspDTO, expression, endDate, startDate);
        } else {
            fill(rspDTO, expression, startDate, endDate);
        }
    }

    private Date parseReferenceDate(String referenceDate) {
        if (referenceDate == null || referenceDate.trim().length() == 0) {
            return new Date();
        }
        try {
            return new SimpleDateFormat(DATE_PATTERN).parse(referenceDate);
        } catch (ParseException e) {
            return new Date();
        }
    }

    private Date parseDate(String value) {
        if (value == null || value.trim().length() == 0) {
            return null;
        }
        try {
            SimpleDateFormat format = new SimpleDateFormat(DATE_PATTERN);
            format.setLenient(false);
            return format.parse(value);
        } catch (ParseException e) {
            return null;
        }
    }

    private Date date(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.setLenient(false);
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month - 1);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        try {
            return calendar.getTime();
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private int year(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.YEAR);
    }

    private int parseQuarter(String cnOrNumber, String qNumber) {
        String value = qNumber == null ? cnOrNumber : qNumber;
        if ("一".equals(value)) {
            return 1;
        }
        if ("二".equals(value)) {
            return 2;
        }
        if ("三".equals(value)) {
            return 3;
        }
        if ("四".equals(value)) {
            return 4;
        }
        return Integer.parseInt(value);
    }

    private String format(Date date) {
        return new SimpleDateFormat(DATE_PATTERN).format(date);
    }
}
