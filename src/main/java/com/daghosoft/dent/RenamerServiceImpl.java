package com.daghosoft.dent;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RenamerServiceImpl implements RenamerService {

    private static final String SPACE = " ";
    private static Set<String> wordSeparatorlist = new HashSet<>();
    private static Set<String> blackListWords = new HashSet<>();
    private int yearLimit = 1900;

    protected ConfigServiceStatic config;

    // Costruttore di test
    public RenamerServiceImpl(ConfigServiceStatic pConfig) {
        config = pConfig;

        String[] ws = config.getWordSeparator().split(";");
        wordSeparatorlist.addAll(Arrays.asList(ws));

        try {
            yearLimit = Integer.valueOf(config.getYearLimit());
        } catch (Exception e) {
        }
        blackListWords = config.getConcatWords();
    }

    public RenamerServiceImpl() {
        this(ConfigServiceStatic.getConfig());
    }

    @Override
    public String rename(final String name, Boolean isFile) {
        String baseName = name.toLowerCase();
        String ext = StringUtils.EMPTY;

        if (isFile) {
            baseName = FilenameUtils.getBaseName(name).toLowerCase();
            ext = "." + FilenameUtils.getExtension(name).toLowerCase();
        }
        LOGGER.debug("!!!!!!!!!!!!!!!!!!!!!!! filename : [{}] + ext : [{}]", baseName, ext);
        if (StringUtils.isBlank(baseName)) {
            return name;
        }
        baseName = removeWordSeparator(baseName);
        LOGGER.debug("Rimossi i separatori : [{}]", baseName);

        baseName = blackListFilter(baseName);
        LOGGER.debug("Rimosse le parole in blackList : [{}]", baseName);

        baseName = formatYear(baseName);
        LOGGER.debug("Gestita la formattazione dell'anno : [{}]", baseName);

        baseName = WordUtils.capitalizeFully(baseName);
        LOGGER.debug("Capitalize : [{}]", baseName);

        baseName = StringUtils.normalizeSpace(baseName);
        LOGGER.debug("Tolti spazi doppi : [{}]", baseName);

        baseName = removeMultipleHyphen(baseName);
        LOGGER.debug("Tolti doppi trattini : [{}]", baseName);

        baseName = normalizeYearSeparator(baseName);
        LOGGER.debug("Rimosso il - prima dell'estenzione : [{}]", baseName);

        baseName = killerHyphenContainingWord(baseName);
        LOGGER.debug("Verfica delle parole che contengono il [-]: [{}]", baseName);

        baseName = normalizeYearSeparator(baseName);
        LOGGER.debug("Rimosso il - prima dell'estenzione : [{}]", baseName);

        String out = baseName + ext;
        LOGGER.debug("Aggiunta estenzione : [{}]", baseName);

        return out.trim();
    }

    protected String formatYear(String baseName) {
        String[] arr = splitOnSpace(baseName);
        StringBuilder out = new StringBuilder();
        for (String s : arr) {
            try {
                Integer year = Integer.valueOf(s);
                if (year >= yearLimit) {
                    out.append(SPACE).append(String.format("(%s) -", s));
                } else {
                    out.append(SPACE).append(s);
                }

            } catch (NumberFormatException e) {
                out.append(SPACE).append(s);
            }
        }
        return out.toString().trim();
    }

    protected String normalizeYearSeparator(String w) {
        String out = w.trim();
        if (out.endsWith("-")) {
            out = out.substring(0, out.lastIndexOf("-")).trim();
        }
        if (out.startsWith("-")) {
            out = out.substring(1, out.length());
        }

        return out.trim();
    }

    protected String removeMultipleHyphen(String w) {
        String out = w.trim();
        out = StringUtils.normalizeSpace(out).replace("- -", "-");
        if (out.contains("- -")) {
            out = removeMultipleHyphen(out);
        }

        return out.trim();
    }

    protected String removeWordSeparator(String fileName) {
        String out = fileName;
        for (String s : wordSeparatorlist) {
            out = out.replace(s, SPACE);
        }
        return out.trim();
    }

    protected String blackListFilter(String fileName) {
        StringBuilder out = new StringBuilder();
        String[] filenameArr = splitOnSpace(fileName);

        for (String s : filenameArr) {
            if (blackListWords.contains(s.toLowerCase())) {
                continue;
            }
            out.append(SPACE).append(s);
        }

        return out.toString().trim();
    }

    /**
     * Verifica la presenza di parole che contengono il [-] se presenti vengono
     * spezzate e processate come singole parole, in quel caso se tutte le parti
     * della parola originaria sono in black list allora la parola viene
     * eliminata. es :
     * 
     * wall-e -> niente da fare | dvdrip-novarip -> va cancellata se nella black
     * list è presente sia dvdrip che novarip | dvdrip-xmen -> niente da fare
     * 
     * @param fileName
     * @return
     */
    protected String killerHyphenContainingWord(String fileName) {
        StringBuilder out = new StringBuilder();
        String[] filenameArr = StringUtils.normalizeSpace(fileName).split(SPACE);

        // Loop sulle parole
        for (String s : filenameArr) {
            // verifico se la parola contiene [-] ma allo stesso tempo deve
            // essere diversa dal solo [-]
            if (s.contains("-") && !s.equals("-")) {
                // booleano di verifica di default è false così che se tutte le
                // parole a seguire non sono contenute nella black list diventa
                // vero
                boolean valid = false;
                // Split in parti e verifica delle singole componenti
                for (String word : s.split("-")) {
                    valid = blackListWords.contains(word.toLowerCase()) ? false : true;
                }
                // se valida entra nella stringa finale senno viene buttata
                if (valid)
                    out.append(SPACE).append(s);
            } else {
                // non contiene[-] quindi la salvo così com'è
                out.append(SPACE).append(s);
            }
        }

        return out.toString().trim();
    }

    private String[] splitOnSpace(String fileName) {
        return fileName.trim().split(SPACE);
    }
}
