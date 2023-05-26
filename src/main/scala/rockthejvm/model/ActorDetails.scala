package rockthejvm.model

import play.api.libs.json.JsValue

case class ActorDetails(
    id: Long,
    actorId: Long,
    personalInfo: JsValue
)
