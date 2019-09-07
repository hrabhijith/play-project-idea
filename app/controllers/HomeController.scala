package controllers

import javax.inject._
import play.api._
import play.api.mvc._
import play.api.db._
import play.api.data._
import play.api.data.Forms._
import play.api.data.validation.Constraints._
import play.api.i18n.I18nSupport
import scalaj.http.{Http, HttpResponse}
import play.api.libs.json._
/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
case class BasicForm(fromLocation: String, toDestination: String)

// this could be defined somewhere else,
// but I prefer to keep it in the companion object
object BasicForm {
  val form: Form[BasicForm] = Form(
    mapping(
      "fromLocation" -> text,
      "toDestination" -> text
    )(BasicForm.apply)(BasicForm.unapply)
  )
}

@Singleton
class HomeController @Inject()(db:Database, cc: ControllerComponents) extends AbstractController(cc) with I18nSupport {

  /**
   * Create an Action to render an HTML page.
   *
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  
  def index() = Action { implicit request: Request[AnyContent] =>
    var outString = "Our name is "
    val conn = db.getConnection()

    try {
      val stmt = conn.createStatement
      val rs = stmt.executeQuery("SELECT username FROM users")

      while (rs.next()) {
        outString += rs.getString("username")
      }
    } finally {
      conn.close()
    }
    Ok(views.html.index(outString))
  }
  
  
  def transportation()= Action{implicit request: Request[AnyContent] => Ok(views.html.transportation()) }

  def searchTransportation() = Action { implicit request =>
  val formData: String = BasicForm.form.bindFromRequest.get.fromLocation
  val toData: String = BasicForm.form.bindFromRequest.get.toDestination

  // from location get call
  val fromUrl : String = "https://maps.googleapis.com/maps/api/geocode/json?address=bengaluru&key=AIzaSyDQzBHFrZTSHduwMpg5wqL_o0YSZ3hvkdg"

  val fromAddressResult = Http(fromUrl).asString 
  val fromJson = Json.parse(fromAddressResult.body)
  // extract lat for from location
  val fromLat=  fromJson("results")(0)("geometry")("location")("lat")
  
  // extract lang for from location
  val fromLng=  fromJson("results")(0)("geometry")("location")("lng")
  
  // to loation url
  val toUrl : String = "https://maps.googleapis.com/maps/api/geocode/json?address=karlsruhe&key=AIzaSyDQzBHFrZTSHduwMpg5wqL_o0YSZ3hvkdg"

  // to location get call
  val toAddressResult =Http(toUrl).asString
   val toJson = Json.parse(toAddressResult.body)
  
   // extract lat for from location
   val toLat=  toJson("results")(0)("geometry")("location")("lat")
  // extract lang for from location
   val toLng=  toJson("results")(0)("geometry")("location")("lng")

  // display all variables down here */
  Ok(fromLat.toString()+ fromLng.toString() + toLat.toString() + toLng.toString()) 
 }

}
