The list of known variants is sorted alphabetically instead of numerically, so we sorted the file ourselves:
https://drive.google.com/file/d/0B9YOv_a79r_PcVpyX3ZOY2NrZjQ/view?usp=sharing
Using the command:
```
sort -k 1,1n -k 2,2n /home/tcs/public_html/COMP555/VCFdata/ALL.merged.phase1_release_v3.20101123.snps_indels_svs.vcf > ../AMSorted.vcf
```
but with the X chromosome removed.
This means we can iterate through both the known variants and candidate variants file simultaneously.


Filtering family variants by exon regions, and inheritance patterns:
```
java InheritanceExonFilter /home/tcs/public_html/COMP555/VCFdata/merged.vcf /home/tcs/public_html/COMP555/VCFdata/wgEncodeGencodeBasicV17.txt > Results/familyVariantsFilteredByInheritanceExon.vcf
```


Mapping each chromosome+position to a locus, for filtering by locus:
```
java LocusMapper Results/familyVariantsFilteredByInheritanceExon.vcf Auxiliary/cytoBand.vcf > Results/mappedCandidates.vcf
```
Filter candidate variants by known loci for all diseases but skeletal dysplasia:
```
java LociFilter Auxiliary/knownDiseaseLoci.vcf Results/mappedCandidates.vcf > Results/candidatesFilteredByKnownLoci.vcf
```
Also filter candidate variants by some known loci for skeletal dysplasia:
```
java LociFilter Auxiliary/skeletalDysplasiaLoci.vcf Results/mappedCandidates.vcf > Results/candidatesFilteredByKnownLoci-skeletal.vcf
```


Filter the non-loci-filtered data by known variants, and extract the population proportions for the known variants (using the sorted known variants file):
```
java KnownVariantFilter ../AMSorted.vcf Results/mappedCandidates.vcf > Results/candidatesFilteredByKnownVariants2.vcf
```


Run bayesian statistics on the variants with population proportions, and sort candidates by bayesian probability (keeping header at the top):
```
java Bayes Results/candidatesFilteredByKnownVariants.vcf | (head -n 1 && tail -n +1 | sort -s -rg -k 1,1) > Results/candidateProbabilities.vcf
```

Separate into VCF files for each disease (for probability file):
```
(head -n 1 && tail -n +1 | grep "SickleCellAnemia") < Results/candidateProbabilities.vcf > Results/SickleCellAnemiaProbabilities.vcf
(head -n 1 && tail -n +1 | grep "RetinitisPigmentosa") < Results/candidateProbabilities.vcf > Results/RetinitisPigmentosaProbabilities.vcf
(head -n 1 && tail -n +1 | grep "SkeletalDysplasia") < Results/candidateProbabilities.vcf > Results/SkeletalDysplasiaProbabilities.vcf

```


Separate into VCF files for each disease (for loci file):
