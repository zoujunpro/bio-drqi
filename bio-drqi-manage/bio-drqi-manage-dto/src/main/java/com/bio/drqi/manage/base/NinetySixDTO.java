package com.bio.drqi.manage.base;

import lombok.Data;

@Data
public class NinetySixDTO {
    /**
     * 一个96公板
     */
    private Row row1 = new Row();
    private Row row2 = new Row();
    private Row row3 = new Row();
    private Row row4 = new Row();
    private Row row5 = new Row();
    private Row row6 = new Row();
    private Row row7 = new Row();
    private Row row8 = new Row();


    @Data
    public static class Row {
        private SampleUnitDTO A1 = new SampleUnitDTO();
        private SampleUnitDTO B2 = new SampleUnitDTO();
        private SampleUnitDTO C3 = new SampleUnitDTO();
        private SampleUnitDTO D4 = new SampleUnitDTO();
        private SampleUnitDTO E5 = new SampleUnitDTO();
        private SampleUnitDTO F6 = new SampleUnitDTO();
        private SampleUnitDTO G7 = new SampleUnitDTO();
        private SampleUnitDTO H8 = new SampleUnitDTO();
        private SampleUnitDTO I9 = new SampleUnitDTO();
        private SampleUnitDTO J10 = new SampleUnitDTO();
        private SampleUnitDTO K11 = new SampleUnitDTO();
        private SampleUnitDTO L12 = new SampleUnitDTO();

        private boolean fillRow(String vectorTaskCode, String transFormCode, String sampleCode,String identifyPrimer) {
            if (this.A1.ifNull()) {
                this.A1.fillData(vectorTaskCode, transFormCode, sampleCode,identifyPrimer);
                return true;
            }
            if (this.B2.ifNull()) {
                this.B2.fillData(vectorTaskCode, transFormCode, sampleCode,identifyPrimer);
                return true;
            }
            if (this.C3.ifNull()) {
                this.C3.fillData(vectorTaskCode, transFormCode, sampleCode,identifyPrimer);
                return true;
            }
            if (this.D4.ifNull()) {
                this.D4.fillData(vectorTaskCode, transFormCode, sampleCode,identifyPrimer);
                return true;
            }
            if (this.E5.ifNull()) {
                this.E5.fillData(vectorTaskCode, transFormCode, sampleCode,identifyPrimer);
                return true;
            }
            if (this.F6.ifNull()) {
                this.F6.fillData(vectorTaskCode, transFormCode, sampleCode,identifyPrimer);
                return true;
            }
            if (this.H8.ifNull()) {
                this.H8.fillData(vectorTaskCode, transFormCode, sampleCode,identifyPrimer);
                return true;
            }
            if (this.I9.ifNull()) {
                this.I9.fillData(vectorTaskCode, transFormCode, sampleCode,identifyPrimer);
                return true;
            }
            if (this.J10.ifNull()) {
                this.J10.fillData(vectorTaskCode, transFormCode, sampleCode,identifyPrimer);
                return true;
            }
            if (this.K11.ifNull()) {
                this.K11.fillData(vectorTaskCode, transFormCode, sampleCode,identifyPrimer);
                return true;
            }
            return false;
        }


    }

    public boolean addLayout(String vectorTaskCode, String transFormCode, String sampleCode, String identifyPrimer) {
        if (this.row1.fillRow(vectorTaskCode, transFormCode, sampleCode,identifyPrimer)) {
            return true;
        }
        if (this.row2.fillRow(vectorTaskCode, transFormCode, sampleCode,identifyPrimer)) {
            return true;
        }
        if (this.row3.fillRow(vectorTaskCode, transFormCode, sampleCode,identifyPrimer)) {
            return true;
        }
        if (this.row4.fillRow(vectorTaskCode, transFormCode, sampleCode,identifyPrimer)) {
            return true;
        }
        if (this.row5.fillRow(vectorTaskCode, transFormCode, sampleCode,identifyPrimer)) {
            return true;
        }
        if (this.row6.fillRow(vectorTaskCode, transFormCode, sampleCode,identifyPrimer)) {
            return true;
        }
        if (this.row7.fillRow(vectorTaskCode, transFormCode, sampleCode,identifyPrimer)) {
            return true;
        }
        if (this.row8.fillRow(vectorTaskCode, transFormCode, sampleCode,identifyPrimer)) {
            return true;
        }
        return false;
    }


}
