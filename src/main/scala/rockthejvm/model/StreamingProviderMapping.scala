package rockthejvm.model

import rockthejvm.model.StreamingProviderMapping.StreamingProvider.StreamingProvider

final case class StreamingProviderMapping(
    id: Long,
    movieId: Long,
    streamingProvider: StreamingProvider
)

object StreamingProviderMapping {

  object StreamingProvider extends Enumeration {
    type StreamingProvider = Value
    val Netflix = Value("Netflix")
    val Hulu = Value("Hulu")
  }

  def tupled = (this.apply _).tupled
}
