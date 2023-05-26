package rockthejvm

import rockthejvm.model.StreamingProviderMapping.StreamingProvider
import rockthejvm.model.StreamingProviderMapping.StreamingProvider.StreamingProvider
import slick.lifted.MappedToBase.mappedToIsomorphism
import slick.lifted.ProvenShape

import java.time.LocalDate

object Tables {

  import rockthejvm.model._
  import CustomPostgresProfile.api._

  class MovieTable(tag: Tag) extends Table[Movie](tag, Some("movies"), "Movie") {

    def id: Rep[Long] = column[Long]("movie_id", O.PrimaryKey, O.AutoInc)
    def name: Rep[String] = column[String]("name")
    def releaseDate: Rep[LocalDate] = column[LocalDate]("release_date")
    def lengthInMin: Rep[Int] = column[Int]("length_in_min")

    override def * : ProvenShape[Movie] =
      (id, name, releaseDate, lengthInMin) <> (Movie.tupled, Movie.unapply)
  }

  class ActorTable(tag: Tag) extends Table[Actor](tag, Some("movies"), "Actor") {

    def id: Rep[Long] = column[Long]("actor_id", O.PrimaryKey, O.AutoInc)
    def name: Rep[String] = column[String]("name")

    override def * : ProvenShape[Actor] =
      (id, name) <> (Actor.tupled, Actor.unapply)
  }

  class MovieActorMappingTable(tag: Tag) extends Table[MovieActorMapping](tag, Some("movies"), "MovieActorMapping") {

    def id: Rep[Long] = column[Long]("movie_actor_id", O.PrimaryKey, O.AutoInc)
    def movieId: Rep[Long] = column[Long]("movie_id")
    def actorId: Rep[Long] = column[Long]("actor_id")

    override def * : ProvenShape[MovieActorMapping] =
      (id, movieId, actorId) <> (MovieActorMapping.tupled, MovieActorMapping.unapply)
  }

  class StreamingProviderMappingTable(tag: Tag) extends Table[StreamingProviderMapping](tag, Some("movies"), "StreamingProviderMapping") {

    implicit val providerMapper =
      MappedColumnType.base[StreamingProvider, String](_.toString, StreamingProvider.withName)

    def id: Rep[Long] = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def movieId: Rep[Long] = column[Long]("movie_id")
    def streamingProvider: Rep[StreamingProvider] = column[StreamingProvider]("streaming_provider")

    override def * : ProvenShape[StreamingProviderMapping] =
      (id, movieId, streamingProvider) <> (StreamingProviderMapping.tupled, StreamingProviderMapping.unapply)
  }

  class MovieLocationsTable(tag: Tag) extends Table[MovieLocations](tag, Some("movies"), "MovieLocations") {

    def id: Rep[Long] = column[Long]("movie_location_id", O.PrimaryKey, O.AutoInc)
    def movieId: Rep[Long] = column[Long]("movie_id")
    def locations: Rep[List[String]] = column[List[String]]("locations")

    override def * : ProvenShape[MovieLocations] =
      (id, movieId, locations) <> (MovieLocations.tupled, MovieLocations.unapply)
  }

  class MoviePropertiesTable(tag: Tag) extends Table[MovieProperties](tag, Some("movies"), "MovieProperties") {

    def id: Rep[Long] = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def movieId: Rep[Long] = column[Long]("movie_id")
    def properties: Rep[Map[String, String]] = column[Map[String, String]]("properties")

    override def * : ProvenShape[MovieProperties] =
      (id, movieId, properties) <> (MovieProperties.tupled, MovieProperties.unapply)
  }

  class ActorDetailsTable(tag: Tag) extends Table[ActorDetails](tag, Some("movies"), "ActorDetails") {

    def id: Rep[Long] = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def movieId: Rep[Long] = column[Long]("actor_id")

    def properties: Rep[Map[String, String]] = column[Map[String, String]]("personal_info")

    override def * : ProvenShape[ActorDetails] =
      (id, movieId, properties) <> (ActorDetails.tupled, ActorDetails.unapply)
  }

  // API entrypoint
  lazy val movieTable = TableQuery[MovieTable]
  lazy val actorTable = TableQuery[ActorTable]
  lazy val movieActorMappingTable = TableQuery[MovieActorMappingTable]
  lazy val streamingProviderMappingTable = TableQuery[StreamingProviderMappingTable]
  lazy val movieLocationsTable = TableQuery[MovieLocationsTable]
  lazy val moviePropertiesTable = TableQuery[MoviePropertiesTable]
  lazy val actorDetailsTable = TableQuery[ActorDetailsTable]

}
