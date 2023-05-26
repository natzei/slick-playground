package rockthejvm

import com.github.tminglei.slickpg.{ExPostgresProfile, PgArraySupport, PgHStoreSupport, PgJsonSupport, PgPlayJsonSupport}

trait CustomPostgresProfile extends ExPostgresProfile with PgHStoreSupport with PgArraySupport with PgJsonSupport with PgPlayJsonSupport {

  override val api = CustomPGAPI
  override val pgjson = "jsonb"

  object CustomPGAPI extends API with HStoreImplicits with ArrayImplicits with JsonImplicits
}

object CustomPostgresProfile extends CustomPostgresProfile
