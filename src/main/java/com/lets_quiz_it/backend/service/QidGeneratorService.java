package com.lets_quiz_it.backend.service;

import com.lets_quiz_it.backend.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class QidGeneratorService {

    private final QuestionRepository questionRepository;

    private static final Map<String, String> TOPIC_PREFIXES = Map.ofEntries(
            // Quantitative Aptitude
            Map.entry("number system & simplification", "NUM"),
            Map.entry("percentage",                     "PCT"),
            Map.entry("ratio & proportion",             "RAT"),
            Map.entry("average",                        "AVG"),
            Map.entry("profit & loss",                  "PNL"),
            Map.entry("discount",                       "DSC"),
            Map.entry("simple interest & compound interest", "INT"),
            Map.entry("time & work",                    "TWK"),
            Map.entry("pipe & cistern",                 "PPC"),
            Map.entry("time, speed & distance",         "TSD"),
            Map.entry("problems on train",              "TRN"),
            Map.entry("boat & stream",                  "BOT"),
            Map.entry("mixture & alligation",           "MIX"),
            Map.entry("partnership",                    "PRT"),
            Map.entry("problems on ages",               "AGE"),
            Map.entry("mensuration",                    "MEN"),
            Map.entry("algebra",                        "ALG"),
            Map.entry("geometry & coordinate geometry", "GEO"),
            Map.entry("trigonometry",                   "TRG"),
            Map.entry("data interpretation",            "DIN"),
            Map.entry("surds & indices",                "SUR"),
            Map.entry("simplification",                 "SMP"),

            // Reasoning
            Map.entry("analogy",                        "ANL"),
            Map.entry("series",                         "SER"),
            Map.entry("coding-decoding",                "COD"),
            Map.entry("blood relations",                "BLD"),
            Map.entry("direction & distance",           "DIR"),
            Map.entry("syllogism",                      "SYL"),
            Map.entry("statement & conclusion",         "STC"),
            Map.entry("order & ranking",                "ORD"),
            Map.entry("calendar & clock",               "CAL"),
            Map.entry("matrix",                         "MTX"),
            Map.entry("venn diagram",                   "VEN"),
            Map.entry("missing number",                 "MSN"),
            Map.entry("non-verbal reasoning",           "NVR"),

            // English
            Map.entry("reading comprehension",          "RDC"),
            Map.entry("fill in the blanks",             "FIB"),
            Map.entry("error spotting",                 "ERR"),
            Map.entry("sentence improvement",           "SIM"),
            Map.entry("para jumbles",                   "PJM"),
            Map.entry("synonyms & antonyms",            "SYN"),
            Map.entry("one word substitution",          "OWS"),
            Map.entry("idioms & phrases",               "IDP"),
            Map.entry("spelling correction",            "SPL"),
            Map.entry("cloze test",                     "CLZ"),

            // General Awareness
            Map.entry("current affairs",                "CUR"),
            Map.entry("history",                        "HIS"),
            Map.entry("geography",                      "GEO"),
            Map.entry("indian polity & constitution",   "POL"),
            Map.entry("economy & budget basics",        "ECO"),
            Map.entry("science",                        "SCI"),
            Map.entry("static gk",                      "SGK"),
            Map.entry("computer basics",                "CPT")
    );

    public String generateQid(List<String> topicNames) {
        // For multi-topic, use the first topic's prefix
        // Or use "MUL" for genuinely cross-topic questions
        String prefix = topicNames.stream()
                .map(n -> TOPIC_PREFIXES.get(n.toLowerCase().trim()))
                .filter(Objects::nonNull)
                .findFirst()
                .orElse("GEN");

        // Count existing questions with this prefix
        long count = questionRepository.countByQidStartingWith(prefix);
        return String.format("%s-%04d", prefix, count + 1);
    }
}
