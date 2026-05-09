package com.bio.drqi.es.support.search;

import com.bio.drqi.domain.BioDict;
import com.bio.drqi.domain.CerBreedDict;
import com.bio.drqi.domain.CerSpeciesConf;
import com.bio.drqi.domain.SeedProduceAddressDict;
import com.bio.drqi.mapper.BioDictMapper;
import com.bio.drqi.mapper.CerBreedDictMapper;
import com.bio.drqi.mapper.CerSpeciesConfMapper;
import com.bio.drqi.mapper.SeedProduceAddressDictMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@ConditionalOnProperty(prefix = "bio.es", name = "enabled", havingValue = "true")
public class GlobalSearchDictTranslator {

    private static Map<String, String> speciesNameMap = Collections.emptyMap();
    private static Map<String, String> breedNameMap = Collections.emptyMap();
    private static Map<String, String> dictNameMap = Collections.emptyMap();
    private static Map<String, String> produceAddressNameMap = Collections.emptyMap();

    private final CerSpeciesConfMapper cerSpeciesConfMapper;
    private final CerBreedDictMapper cerBreedDictMapper;
    private final BioDictMapper bioDictMapper;
    private final SeedProduceAddressDictMapper seedProduceAddressDictMapper;

    public GlobalSearchDictTranslator(CerSpeciesConfMapper cerSpeciesConfMapper,
                                      CerBreedDictMapper cerBreedDictMapper,
                                      BioDictMapper bioDictMapper,
                                      SeedProduceAddressDictMapper seedProduceAddressDictMapper) {
        this.cerSpeciesConfMapper = cerSpeciesConfMapper;
        this.cerBreedDictMapper = cerBreedDictMapper;
        this.bioDictMapper = bioDictMapper;
        this.seedProduceAddressDictMapper = seedProduceAddressDictMapper;
    }

    @PostConstruct
    public void init() {
        refresh();
    }

    public void refresh() {
        Map<String, String> speciesMap = new HashMap<>();
        List<CerSpeciesConf> speciesList = cerSpeciesConfMapper.selectAll();
        if (speciesList != null) {
            for (CerSpeciesConf species : speciesList) {
                if (notEmpty(species.getSpeciesCode())) {
                    speciesMap.put(species.getSpeciesCode(), species.getSpeciesName());
                }
            }
        }

        Map<String, String> breedMap = new HashMap<>();
        List<CerBreedDict> breedList = cerBreedDictMapper.selectAll();
        if (breedList != null) {
            for (CerBreedDict breed : breedList) {
                if (notEmpty(breed.getBreedCode())) {
                    breedMap.put(breed.getBreedCode(), breed.getBreedName());
                    if (notEmpty(breed.getSpeciesCode())) {
                        breedMap.put(breed.getSpeciesCode() + ":" + breed.getBreedCode(), breed.getBreedName());
                    }
                }
            }
        }

        Map<String, String> dictMap = new HashMap<>();
        List<BioDict> dictList = bioDictMapper.selectAll();
        if (dictList != null) {
            for (BioDict dict : dictList) {
                if (notEmpty(dict.getDictType()) && notEmpty(dict.getDictValueCode())) {
                    dictMap.put(dict.getDictType() + ":" + dict.getDictValueCode(), dict.getDictValueName());
                }
            }
        }

        Map<String, String> produceAddressMap = new HashMap<>();
        List<SeedProduceAddressDict> addressList = seedProduceAddressDictMapper.selectAll();
        if (addressList != null) {
            for (SeedProduceAddressDict address : addressList) {
                if (notEmpty(address.getAddressCode())) {
                    produceAddressMap.put(address.getAddressCode(), address.getAddressName());
                }
            }
        }

        speciesNameMap = speciesMap;
        breedNameMap = breedMap;
        dictNameMap = dictMap;
        produceAddressNameMap = produceAddressMap;
        log.info("全局搜索字典缓存加载完成 species={}, breed={}, dict={}, produceAddress={}",
                speciesNameMap.size(), breedNameMap.size(), dictNameMap.size(), produceAddressNameMap.size());
    }

    public static String speciesName(Object value) {
        String code = stringValue(value);
        String name = speciesNameMap.get(code);
        return notEmpty(name) ? name : code;
    }

    public static String breedName(Object value) {
        String code = stringValue(value);
        String name = breedNameMap.get(code);
        return notEmpty(name) ? name : code;
    }

    public static String breedName(Object speciesCode, Object breedCode) {
        String species = stringValue(speciesCode);
        String breed = stringValue(breedCode);
        String name = breedNameMap.get(species + ":" + breed);
        if (notEmpty(name)) {
            return name;
        }
        return breedName(breed);
    }

    public static String dictName(Object dictType, Object dictValueCode) {
        String type = stringValue(dictType);
        String code = stringValue(dictValueCode);
        String name = dictNameMap.get(type + ":" + code);
        return notEmpty(name) ? name : code;
    }

    public static String produceAddressName(Object value) {
        String code = stringValue(value);
        String name = produceAddressNameMap.get(code);
        return notEmpty(name) ? name : code;
    }

    private static String stringValue(Object value) {
        return value == null ? "" : String.valueOf(value).trim();
    }

    private static boolean notEmpty(String value) {
        return value != null && !value.trim().isEmpty();
    }
}
