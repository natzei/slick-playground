package rockthejvm.model

case class MovieProperties(
    id: Long,
    movieId: Long,
    properties: Map[String, String]
)
