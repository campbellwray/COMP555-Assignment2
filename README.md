The list of known variants is sorted alphabetically instead of numerically, so we sorted the file ourselves:
    https://drive.google.com/file/d/0B9YOv_a79r_PcVpyX3ZOY2NrZjQ/view?usp=sharing
This means we can iterate through both the known variants and candidate variants file simultaneously.


Filtering family variants by exon regions, and inheritance patterns:
java InheritanceExonFilter /home/tcs/public_html/COMP555/VCFdata/merged.vcf /home/tcs/public_html/COMP555/VCFdata/wgEncodeGencodeBasicV17.txt > Results/familyVariantsFilteredByInheritanceExon.vcf

Mapping each chromosome+position to a locus, for filtering by locus
java LocusMapper Results/familyVariantsFilteredByInheritanceExon.vcf Auxiliary/cytoBand.vcf > Results/mappedCandidates.vcf

Filter candidate variants by known loci for all diseases but skeletal dysplasia
java LociFilter Auxiliary/knownDiseaseLoci.vcf Results/mappedCandidates.vcf > Results/candidatesFilteredByKnownLoci.vcf

Also filter candidate variants by some known loci for skeletal dysplasia
java LociFilter Auxiliary/skeletalDysplasiaLoci.vcf Results/mappedCandidates.vcf > Results/candidatesFilteredByKnownLoci-skeletal.vcf



Filter the non-loci-filtered data by known variants, and extract the population proportions for the known variants


Run bayesian statistics on the variants with population proportions


Sort candidates by bayesian probability


Separate files into each disease
--for probability file
--for loci file