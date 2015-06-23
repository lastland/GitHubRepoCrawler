/**
 * Created by lastland on 15/6/23.
 */
import net.ruippeixotog.scalascraper.browser.Browser
import org.jsoup.HttpStatusException
import org.jsoup.nodes.Document

import scala.util.{Failure, Success, Try}

object PageGetter {
  def get(link: String): Try[Document] = {
    val browser = new Browser
    get(browser, link)
  }

  def get(browser: Browser, link: String): Try[Document] = {
    get(browser, link, 1000)
  }

  protected def get(browser: Browser, link: String, sleepTime: Int): Try[Document] = {
    try {
      Success(browser.get(link))
    } catch {
      case statusException: HttpStatusException =>
        if (statusException.getStatusCode == 429) {
          Thread.sleep(sleepTime)
          get(browser, link, List(sleepTime * 2, 60000).min)
        } else {
          Failure(statusException)
        }
    }
  }
}
