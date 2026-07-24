-keepattributes Signature,*Annotation*

# Gson maps API DTOs and persisted state to JSON by field name, so every field name
# here must survive R8 verbatim and no field may be stripped. Constructors are pinned
# too, since Gson instantiates these types reflectively. Method bodies are still fair
# game for shrinking and obfuscation. Package-wide on purpose: a DTO added to this
# package is covered without anyone remembering to touch this file.
#
# Verify after changing this rule: every property name of the data classes in
# data/Models.kt, data/ApiClient.kt and data/LocalStore.kt must still appear in the
# release DEX string pool, otherwise persisted state silently fails to load.
-keep class one.echobell.echobellandroid.data.** {
  <fields>;
  <init>(...);
}
