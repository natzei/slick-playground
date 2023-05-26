package rockthejvm

import rockthejvm.Main.saveTransactionally
import rockthejvm.model.StreamingProviderMapping.StreamingProvider
import rockthejvm.model.StreamingProviderMapping.StreamingProvider.StreamingProvider
import rockthejvm.model._
import slick.dbio.Effect.Write
import slick.jdbc.GetResult

import java.time.LocalDate
import scala.concurrent.{ExecutionContext, ExecutionContextExecutor, Future}
import scala.util.{Failure, Random, Success}

object PrivateExecutionContext {
  implicit val ec: ExecutionContextExecutor = ExecutionContext.global
}

object Main {

  import CustomPostgresProfile.api._
  import PrivateExecutionContext._

  val shawshank = Movie(1L, "Shawshank Redemption", LocalDate.of(1994, 4, 2), 162)

  def insertMovie(movie: Movie): Future[Int] = {
    val insertQuery: DBIOAction[Int, NoStream, Write] = Tables.movieTable += movie
    Connection.db.run(insertQuery)
  }

  def insertActor(actor: Actor): Future[Int] = {
    val insertQuery: DBIOAction[Int, NoStream, Write] = Tables.actorTable += actor
    Connection.db.run(insertQuery)
  }

  def readAllMovies(): Future[Seq[Movie]] = {
    val allMovies = Tables.movieTable.result
    Connection.db.run(allMovies)
  }

  def readAllActors(): Future[Seq[Actor]] = {
    val allMovies = Tables.actorTable.result
    Connection.db.run(allMovies)
  }

  def readSomeMovies(): Future[Seq[Movie]] = {
    val someMovies = Tables.movieTable.filter(_.name.like("%Redem%")).result
    Connection.db.run(someMovies)
  }

  def demoUpdate(): Future[Int] = {
    val updateDescription = Tables.movieTable.filter(_.name.like("%Redem%")).map(_.lengthInMin).update(120)
    Connection.db.run(updateDescription)
  }

  def rawSelect(): Future[Seq[Movie]] = {
    implicit val getResultMovie: GetResult[Movie] =
      GetResult.apply(pr => Movie(pr.<<, pr.<<, LocalDate.parse(pr.<<), pr.<<))
    val query = sql"""Select * from movies."Movie" """.as[Movie]
    Connection.db.run(query)
  }

  def saveTransactionally(
      movie: Movie,
      actor: Actor,
      provider: StreamingProvider,
      locations: List[String],
      properties: Map[String, String]
  ) = {
    import Tables._

    val combined = (for {
      movie <- movieTable.returning(movieTable) += movie
      actor <- actorTable.returning(actorTable) += actor
      mapping <- movieActorMappingTable.returning(movieActorMappingTable) += MovieActorMapping(id = 0L, movieId = movie.id, actorId = actor.id)
      provider <- streamingProviderMappingTable.returning(streamingProviderMappingTable) +=
        StreamingProviderMapping(id = 0L, movieId = movie.id, streamingProvider = provider)
      locations <- movieLocationsTable.returning(movieLocationsTable) += MovieLocations(id = 0L, movieId = movie.id, locations = locations)
      properties <- moviePropertiesTable.returning(moviePropertiesTable) += MovieProperties(id = 0L, movieId = movie.id, properties)
    } yield (movie, actor, mapping, provider, locations, properties))

    Connection.db.run(combined.transactionally).andThen {
      case Failure(exception) => exception.printStackTrace()
      case Success((movie, actor, mapping, provider, locations, properties)) => println(s"""Transactionally inserted:
          |- actor $actor
          |- movie $movie
          |- mapping $mapping
          |- provider $provider
          |- locations $locations
          |- properties $properties
          |""".stripMargin)
    }
  }

  def main(args: Array[String]): Unit = {

    val futureComputation = for {
      _ <- Connection.db.run(Tables.actorTable.delete)
      _ <- Connection.db.run(Tables.movieTable.delete)
      _ <- Connection.db.run(Tables.movieActorMappingTable.delete)
      _ <- Connection.db.run(Tables.movieLocationsTable.delete)
      _ <- Connection.db.run(Tables.moviePropertiesTable.delete)
      _ <- saveTransactionally(
        actor = Actor(0L, "Nicolas Cage"),
        movie = Movie(id = 0L, name = "Movie name", releaseDate = LocalDate.now(), lengthInMin = Random.nextInt(50) + 100),
        provider = StreamingProvider.Netflix,
        locations = List("Cagliari", "Helsinki"),
        properties = Map("hello" -> "world")
      )
      movies <- readAllMovies()
      actors <- readAllActors()
    } yield {
      println("All movies")
      movies.map(println)
      actors.map(println)
    }

    futureComputation.onComplete {
      case Failure(exception) => println(s"Query execution failed: Reason $exception")
      case Success(_)         => println(s"Read all ok")
    }

    Thread.sleep(5000)
  }
}
