/*
 Copyright (c) UICHUIMI 02/2016

 This file is part of WhiteSuit.

 WhiteSuit is free software: you can redistribute it and/or modify it under the terms of the
 GNU General Public License as published by the Free Software Foundation, either version 3 of
 the License, or (at your option) any later version.

 WhiteSuit is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 See the GNU General Public License for more details.

 You should have received a copy of the GNU General Public License along with Foobar.
 If not, see <http://www.gnu.org/licenses/>.
 */

package core;

import core.vcf.Variant;
import core.vcf.VcfFile;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Lorente-Arencibia, Pascual (pasculorente@gmail.com)
 */
public class Annotator extends WTask {
    public final static String[] HEADERS = {
            "##INFO=<ID=GENE,Number=1,Type=String,Description=\"Ensemble gene ID\">",
            "##INFO=<ID=FEAT,Number=1,Type=String,Description=\"Ensemble feature ID\">",
            "##INFO=<ID=TYPE,Number=1,Type=String,Description=\"Type of feature (Transcript, RegulatoryFeature, MotifFeature)\">",
            "##INFO=<ID=CONS,Number=1,Type=String,Description=\"Consequence type\">",
            "##INFO=<ID=CDNA,Number=1,Type=Integer,Description=\"Relative position of base pair in cDNA sequence\">",
            "##INFO=<ID=CDS,Number=1,Type=Integer,Description=\"Relative position of base pair in coding sequence\">",
            "##INFO=<ID=PROT,Number=1,Type=Integer,Description=\"Relative position of amino acid in protein\">",
            "##INFO=<ID=AMINO,Number=1,Type=String,Description=\"Amino acid change. Only given if the variation affects the protein-coding sequence\">",
            "##INFO=<ID=COD,Number=1,Type=String,Description=\"The alternative codons\">",
            "##INFO=<ID=DIST,Number=1,Type=String,Description=\"Shortest distance from variant to transcript\">",
            "##INFO=<ID=STR,Number=1,Type=String,Description=\"The DNA strand (1 or -1) on which the transcript/feature lies\">",
            "##INFO=<ID=SYMBOL,Number=1,Type=String,Description=\"Gene symbol or name\">",
            "##INFO=<ID=SRC,Number=1,Type=String,Description=\"The source of the gene symbol\">",
            "##INFO=<ID=ENSP,Number=1,Type=String,Description=\"Ensembl protein identifier of the affected transcript\">",
            "##INFO=<ID=SWPR,Number=1,Type=String,Description=\"UniProtKB/Swiss-Prot identifier of protein product\">",
            "##INFO=<ID=TRBL,Number=1,Type=String,Description=\"UniProtKB/TrEMBL identifier of protein product\">",
            "##INFO=<ID=UNI,Number=1,Type=String,Description=\"UniParc identifier of protein product\">",
            "##INFO=<ID=HGVSc,Number=1,Type=String,Description=\"HGVS coding sequence name\">",
            "##INFO=<ID=HGVSp,Number=1,Type=String,Description=\"HGVS protein sequence name\">",
            "##INFO=<ID=SIFTs,Number=1,Type=String,Description=\"SIFT score\">",
            "##INFO=<ID=SIFTp,Number=1,Type=String,Description=\"SIFT prediction\">",
            "##INFO=<ID=PPHs,Number=1,Type=String,Description=\"Polyphen score\">",
            "##INFO=<ID=PPHp,Number=1,Type=String,Description=\"Polyphen prediction\">",
            "##INFO=<ID=POLY,Number=1,Type=String,Description=\"PolyPhen prediction and/or score\">",
            "##INFO=<ID=MTFN,Number=1,Type=String,Description=\"source and identifier of a transcription factor binding profile aligned at this position\">",
            "##INFO=<ID=MTFP,Number=1,Type=String,Description=\"relative position of the variation in the aligned TFBP\">",
            "##INFO=<ID=HIP,Number=0,Type=Flag,Description=\"a flag indicating if the variant falls in a high information position of a transcription factor binding profile (TFBP)\">",
            "##INFO=<ID=MSC,Number=1,Type=String,Description=\"difference in motif score of the reference and variant sequences for the TFBP\">",
            "##INFO=<ID=CLLS,Number=1,Type=String,Description=\"List of cell types and classifications for regulatory feature\">",
            "##INFO=<ID=CANON,Number=0,Type=Flag,Description=\"Transcript is denoted as the canonical transcript for this gene\">",
            "##INFO=<ID=CCDS,Number=1,Type=String,Description=\"CCDS identifer for this transcript, where applicable\">",
            "##INFO=<ID=INTR,Number=1,Type=String,Description=\"Intron number (out of total number)\">",
            "##INFO=<ID=EXON,Number=1,Type=String,Description=\"Exon number (out of total number)\">",
            "##INFO=<ID=DOM,Number=1,Type=String,Description=\"the source and identifer of any overlapping protein domains\">",
            "##INFO=<ID=IND,Number=1,Type=String,Description=\"Individual name\">",
            "##INFO=<ID=ZYG,Number=1,Type=String,Description=\"Zygosity of individual genotype at this locus\">",
            "##INFO=<ID=SV,Number=1,Type=String,Description=\"IDs of overlapping structural variants\">",
            "##INFO=<ID=FRQ,Number=1,Type=String,Description=\"Frequencies of overlapping variants used in filtering\">",
            "##INFO=<ID=MINOR_ALLELE_FREQ,Number=1,Type=String,Description=\"Minor allele and frequency of existing variation in 1000 Genomes Phase 1\">",
            "##INFO=<ID=AFR_MAF,Number=1,Type=String,Description=\"Minor allele and frequency of existing variation in 1000 Genomes Phase 1 combined African population\">",
            "##INFO=<ID=AMR_MAF,Number=1,Type=String,Description=\"Minor allele and frequency of existing variation in 1000 Genomes Phase 1 combined American population\">",
            "##INFO=<ID=ASN_MAF,Number=1,Type=String,Description=\"Minor allele and frequency of existing variation in 1000 Genomes Phase 1 combined Asian population\">",
            "##INFO=<ID=EUR_MAF,Number=1,Type=String,Description=\"Minor allele and frequency of existing variation in 1000 Genomes Phase 1 combined European population\">",
            "##INFO=<ID=AA_MAF,Number=1,Type=String,Description=\"Minor allele and frequency of existing variant in NHLBI-ESP African American population\">",
            "##INFO=<ID=EA_MAF,Number=1,Type=String,Description=\"Minor allele and frequency of existing variant in NHLBI-ESP European American population\">",
            "##INFO=<ID=CLIN,Number=1,Type=String,Description=\"Clinical significance of variant from dbSNP\">",
            "##INFO=<ID=BIO,Number=1,Type=String,Description=\"Biotype of transcript or regulatory feature\">",
            "##INFO=<ID=TSL,Number=1,Type=String,Description=\"Transcript support level\">",
            "##INFO=<ID=PUBM,Number=1,Type=String,Description=\"Pubmed ID(s) of publications that cite existing variant\">",
            "##INFO=<ID=SOMA,Number=1,Type=String,Description=\"Somatic status of existing variation(s)\">"
    };

    public static final String[] FREQUENCIES = {"minor_allele_freq", "amr_maf", "asn_maf", "eur_maf", "afr_maf", "ea_maf", "aa_maf"};
    public static final int MAX_VARIANTS = 1000;
    private final VcfFile vcfFile;
    private final File output;
    private List<Variant> variants;

    public Annotator(File input, File output) {
        this.output = output;
        vcfFile = new VcfFile(input);
        variants = vcfFile.getVariants();
    }

    @Override
    public void start() {
        setTitle("Annotating");
        injectVEPHeaders();
        annotateVariants();
        vcfFile.save(output);
        setTitle("Annotation successful");
    }

    private void injectVEPHeaders() {
        Arrays.stream(HEADERS).forEach(vcfFile.getHeader()::addHeader);
    }

    private Variant getVariant(List<Variant> variants, String[] input) {
        return variants.stream()
                .filter(variant -> variant.getPos() == Integer.valueOf(input[1]) && variant.getChrom().equals(input[0]))
                .findFirst().orElse(null);
    }

    private boolean annotateVariants() {
        final List<Integer> starts = getStarts();
        final AtomicInteger total = new AtomicInteger();
        starts.parallelStream().forEachOrdered(start -> {
            try {
                annotate(start);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return true;
    }

    private List<Integer> getStarts() {
        final List<Integer> starts = new ArrayList<>();
        for (int j = 0; j < variants.size(); j += MAX_VARIANTS) starts.add(j);
        return starts;
    }

    private void annotate(int from) throws Exception {
        int to = from + MAX_VARIANTS;
        if (to >= variants.size()) to = variants.size();
        final List<Variant> subList = variants.subList(from, to);
        println("Annotating " + from + "-" + to);
        final String response = makeHttpRequest(subList);
        annotateVariants(response, subList);
    }

    private String makeHttpRequest(List<Variant> variants) {
        final JSONObject message = getJsonMessage(variants);

        final String server = "http://grch37.rest.ensembl.org/vep/human/region";
        final String postBody = message.toString();
        try {
            final URL url = new URL(server);
            final HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
            httpConnection.setRequestMethod("POST");
            httpConnection.setRequestProperty("Content-Type", "application/json");
            httpConnection.setRequestProperty("Accept", "application/json");
            httpConnection.setRequestProperty("Content-Length", Integer.toString(postBody.getBytes().length));
            httpConnection.setUseCaches(false);
            httpConnection.setDoOutput(true);
            try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(httpConnection.getOutputStream()))) {
                writer.write(postBody);
            }
            final int responseCode = httpConnection.getResponseCode();
            if (responseCode != 200) {
                println("VEP service unavailable");
                throw new RuntimeException("Response code was not 200. Detected response was " + responseCode);
            }
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(httpConnection.getInputStream()))) {
                final StringBuilder builder = new StringBuilder();
                reader.lines().forEach(builder::append);
//                System.out.println(builder.toString());
                return builder.toString();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private JSONObject getJsonMessage(List<Variant> variants) {
        final JSONArray array = getJsonVariantArray(variants);
        final JSONObject message = new JSONObject();
        // {"variants":array}
        message.put("variants", array);
        return message;
    }

    private JSONArray getJsonVariantArray(List<Variant> variants) {
        // Translate list into JSON
        // ["1 156897 156897 A/C","2 3547966 3547968 TCC/T"]
        final JSONArray array = new JSONArray();
        for (Variant v : variants) {
            int start = v.getPos();
            int end = v.getPos() + v.getRef().length() - 1;
            // 1 156897 156897 A/C
            // 2 3547966 3547968 TCC/T
            array.put(String.format("%s %d %d %s/%s", v.getChrom(), start, end, v.getRef(), v.getAlt()));
        }
        return array;
    }

    private Map<String, String> getRequestMap() {
        final Map<String, String> requestMap = new HashMap<>();
        requestMap.put("Content-Type", "application/json");
        return requestMap;
    }

    private void annotateVariants(String response, List<Variant> variants) {
        if (response != null) {
            JSONArray json = new JSONArray(response);
            mapVepInfo(json, variants);
        }
    }

    private void mapVepInfo(JSONArray json, List<Variant> variants) {
        // To go faster, as Vep does not guarantee the order of variants, I will copy the list of variants
        // Then, each located variant will be removed from list
        final List<Variant> copy = new LinkedList<>(variants);
        for (int i = 0; i < json.length(); i++) {
//            System.out.println(copy.hashCode() + " " + i);
            try {
                JSONObject object = json.getJSONObject(i);
                // 1 156897 156897 A/C
                final String[] input = ((String) object.get("input")).split("\\s+");
                final Variant variant = getVariant(copy, input);
                if (variant != null) {
                    incorporateData(variant, object);
                    copy.remove(variant);
                }
            } catch (JSONException | NumberFormatException ex) {
                ex.printStackTrace();

            }
        }
    }

    private void addIntergenicConsequences(Variant variant, JSONObject json) {
    /*
     Third try: intergenic consequences:[array]
     - "variant_allele":"G"
     - "consequence_terms" : [ array ]
     -- "intergenic_variant"
     */
        if (json.has("intergenic_consequences")) {
            final JSONArray cons = json.getJSONArray("intergenic_consequences");
            variant.setInfo("CONS", cons.getJSONObject(0).getJSONArray("consequence_terms").toString());
//            findAndPut(cons.getJSONArray(0), "consequence_terms", variant, "CONS", String.class);
        }
    }

    private synchronized void incorporateData(Variant variant, JSONObject json) {
        addFrequencyData(variant, json);
        addTranscriptConsequences(variant, json);
        addIntergenicConsequences(variant, json);
    }

    private void addTranscriptConsequences(Variant variant, JSONObject json) {
    /*
     Second: "transcript_consequences" : [ array ]
     - "variant_allele":"A"
     - "strand":1

     - "gene_id":"ENSG00000107295"
     - "gene_symbol_source":"HGNC"
     - "gene_symbol":"SH3GL2"
     - "biotype":"protein_coding"

     - "hgnc_id":10831

     - "transcript_id":"ENST00000380607"
     - "codons":"Ccc/Tcc"
     - "amino_acids":"P/S"
     - "protein_start":220
     - "protein_end":220
     - "cds_start":658
     - "cds_end":658
     - "cdna_start":495
     - "cdna_end":739
     - "distance":4425

     - "sift_score":0
     - "sift_prediction":"deleterious"
     - "polyphen_score":0.81
     - "polyphen_prediction":"possibly_damaging"

     - "consequence_terms":[ array ]
     -- "intron_variant","downstream_gene_variant"
     */
        if (json.has("transcript_consequences")) {

            JSONArray cons = json.getJSONArray("transcript_consequences");
            // Only take the first one
            JSONObject first = cons.getJSONObject(0);

            findAndPut(first, "gene_symbol", variant, "SYMBOL", String.class);
            findAndPut(first, "gene_id", variant, "GENE", String.class);
            findAndPut(first, "distance", variant, "DIST", Integer.class);
            findAndPut(first, "biotype", variant, "BIO", String.class);
            findAndPut(first, "transcript_id", variant, "FEAT", String.class);
            findAndPut(first, "codons", variant, "COD", String.class);
            findAndPut(first, "amino_acids", variant, "AMINO", String.class);
            // Unnecessary
//            findAndPut(first, "protein_start", v, "PROTS", Integer.class);
//            findAndPut(first, "protein_end", v, "PROTE", Integer.class);
//            findAndPut(first, "cds_start", v, "CDSS", Integer.class);
//            findAndPut(first, "cds_end", v, "CDSE", Integer.class);
//            findAndPut(first, "cdna_start", v, "CDNAS", Integer.class);
//            findAndPut(first, "cdna_end", v, "CDNAE", Integer.class);
            findAndPut(first, "sift_score", variant, "SIFTs", Double.class);
            findAndPut(first, "sift_prediction", variant, "SIFTp", String.class);
            findAndPut(first, "polyphen_score", variant, "PPHs", Double.class);
            findAndPut(first, "polyphen_prediction", variant, "PPHp", String.class);
            if (first.has("consequence_terms")){
                JSONArray consequenceTerms = first.getJSONArray("consequence_terms");
                final StringBuilder builder = new StringBuilder(consequenceTerms.getString(0));
                int i = 1;
                while (i < consequenceTerms.length()) builder.append(",").append(consequenceTerms.get(i++));
                variant.setInfo("CONS", builder.toString());
            }
        }
    }

    private void addFrequencyData(Variant variant, JSONObject json) {
    /*
     - "id":"temp"
     - "input":"1 1534738 1534738 G/A"
     - "assembly_name":"GRCh37"
     - "seq_region_name":"13"
     - "start":77999693
     - "end":77999693
     - "strand":1
     - "allele_string":"A/G"
     - "most_severe_consequence" : "intergenic_variant"
     - "intergenic_consequences" : [ array ]
     - "colocated_variants" : [ array ]
     - "transcript_consequences" : [ array ]
     */
        /*
         First: "colocated_variants" : [ array ]
         - "id":"rs1536074"
         - "seq_region_name":"9"
         - "start":17721795
         - "end":17721795
         - "somatic":0
         - "strand":1

         - "minor_allele":"T"
         - "allele_string":"T/A"
         - "minor_allele_freq":0.0023

         - "aa_maf":0
         - "ea_maf":0.002285
         - "amr_maf":0.25
         - "asn_maf":0.07
         - "afr_maf":0.03
         - "eur_maf":0.99
         - "ea_allele":"T"
         - "aa_allele":"T"
         - "amr_allele":"G"
         - "afr_allele":"G"
         - "eur_allele":"G"
         - "asn_allele":"G"
        */
        if (json.has("colocated_variants")) {
            final JSONArray variants = json.getJSONArray("colocated_variants");
            variants.forEach(vari -> {
                final JSONObject var = (JSONObject) vari;
                // ID goes in the VCF id field
                final String id = var.getString("id");
                if (variant.getId().equals(".")) variant.setId(id);

                Arrays.stream(FREQUENCIES).filter(var::has).forEach(freq -> {
                    double doubleFrequency = var.optDouble(freq);
                    if (doubleFrequency == Double.NaN && var.getString(freq).contains(":"))
                        doubleFrequency = Double.valueOf(var.getString(freq).split(":")[1]);
                    variant.setInfo(freq.toUpperCase(), String.valueOf(doubleFrequency));
                });
            });

        }
    }

    /**
     * Checks if sourceKey is present in the source JSONObject. In that case, reads a classType
     * object and puts it into target variant with targetKey.
     *
     * @param source    source JSONObject
     * @param sourceKey key in the source JSONObject
     * @param variant   target variant
     * @param targetKey key in the target variant
     * @param classType type of value in source JSONObject
     */
    private void findAndPut(JSONObject source, String sourceKey, Variant variant,
                            String targetKey, Class classType) {
        if (source.has(sourceKey))
            if (classType == String.class)
                variant.setInfo(targetKey, source.getString(sourceKey));
            else if (classType == Integer.class)
                variant.setInfo(targetKey, source.getInt(sourceKey) + "");
            else if (classType == Double.class)
                variant.setInfo(targetKey, source.getDouble(sourceKey) + "");
            else if (classType == Boolean.class)
                variant.setInfo(targetKey, source.getBoolean(sourceKey) + "");
            else if (classType == Long.class)
                variant.setInfo(targetKey, source.getLong(sourceKey) + "");
            else
                variant.setInfo(targetKey, String.valueOf(source.get(sourceKey)));
    }
}
