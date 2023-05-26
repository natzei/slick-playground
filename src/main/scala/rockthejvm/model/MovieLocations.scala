package rockthejvm.model

case class MovieLocations(
    id: Long,
    movieId: Long,
    locations: List[String]
)
