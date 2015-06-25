package com.twitter.server.handler

import com.twitter.finagle.Service
import com.twitter.server.util.HttpUtils._
import com.twitter.server.util.JsonConverter
import com.twitter.util.Future
import com.twitter.util.registry.{Formatter, GlobalRegistry}

/**
 * A [[com.twitter.finagle.Service]] for displaying the current state of the
 * registry.
 *
 * It's intended to be used as a handler for TwitterServer, and
 * doesn't take any arguments yet.  As an admin endpoint, it displays
 * the entire registry in JSON.
 */
class RegistryHandler extends Service[Request, Response] {

  // populate the registry with env and sys properties when handler is loaded
  sys.env.foreach { case (key, value) =>
    GlobalRegistry.get.put(Seq("env", key), value)
  }
  sys.props.foreach { case (key, value) =>
    GlobalRegistry.get.put(Seq("system.properties", key), value)
  }

  // TODO: have nice default HTML rendering for json output
  def apply(req: Request): Future[Response] = {
    newOk(jsonResponse())
  }

  private[handler] def jsonResponse(): String = {
    val registry = GlobalRegistry.get
    JsonConverter.writeToString(Formatter.asMap(registry))
  }
}
