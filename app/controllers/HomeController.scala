package controllers

import javax.inject._
import play.api._
import play.api.mvc._
import play.api.db._
import play.api.data._
import play.api.data.Forms._
import play.api.data.validation.Constraints._
import play.api.i18n.I18nSupport
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
  
  def transportation()= Action{implicit request: Request[AnyContent] => Ok(views.html.transportation(BasicForm.form)) }

  def searchTransportation() = Action { implicit request =>
  val formData1: String = BasicForm.form.bindFromRequest.get.fromLocation
  val formData2: String = BasicForm.form.bindFromRequest.get.toDestination
  // Google api here
  // Careful: BasicForm.form.bindFromRequest returns an Option
  Ok(formData1.toString+" "+formData2.toString) // just returning the data because it's an example :)
 }

}
