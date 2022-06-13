package academy.bangkit.c22.px442.smartfarm.presentation.utils

class PestisidaHelper {
    companion object {
        fun pestisidaFromResult(result: String): Array<String> {
            return when(result) {
                "Bacterialblight" -> arrayOf("Pestisida : Nordox", "The way NORDOX 56 WP works is as a fungicide and bactericide. The active ingredients contained in it are microelements Cu and Ca / mg, can have a \"Tonic\" effect on soils that have been deficient in nutrients.")
                "Blast" -> arrayOf("Pestisida : Corin", "CORRIN is a Biological Pesticide or Biological Agent, based on antagonistic bacteria (Corynebacterium) that is very effective for controlling major diseases in rice and vegetable crops such as crackles, blasts and fungi.")
                "Brownspot" -> arrayOf("Pestisida : Folicur 430 SC", "Folicur 430 SC actively made from tebukonazol 430 g/l is a fungicide that is protective, curative, eradicative and a growth regulator in suspension-sensitive plants. This fungicide can be used to control rice leaf brown spot disease caused by fungi.")
                "Tungro" -> arrayOf("Pestisida : Eco Sida", "It is a formulation between fungicides, insecticides and bactericides that are very efficient for treating diseases in plants caused by fungi, insect and bacteria.")
                else -> arrayOf("", "")
            }
        }
    }
}