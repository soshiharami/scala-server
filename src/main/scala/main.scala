import java.io.InputStream
import java.net.ServerSocket
import java.nio.charset.StandardCharsets

import scala.annotation.tailrec

object main {
    def main(args: Array[String]): Unit = {
        println("Hello HTTP Server!Strat----->")
        val serverSocket = new ServerSocket(8000)
        println("Serving HTTP on localhost port 8000 ...")
        while (true) {
            val socket = serverSocket.accept()

            val input = socket.getInputStream
            @tailrec
            def readUntilEnd(is: InputStream, acc: List[Int] = Nil):String ={
                is.read :: acc match {
                    case x if x.take(4) == List(10,13,10,13) => x.reverse.map(_.toChar).mkString //10,13は改行
                    case x                                   => readUntilEnd(is, x)
                }
            }

            val output = socket.getOutputStream
            val CRLF = "\r\n"
            val responseBody = readUntilEnd(input)
            val responseBodySize = responseBody.length
            val responseText = "HTTP/1.1 200 OK" + CRLF +
                "Content-Length: " + responseBodySize + CRLF +
                "Content-Type: text/plain" + CRLF +
                CRLF +
                responseBody
            output.write(responseText.getBytes(StandardCharsets.UTF_8))

            input.close()
            output.close()
        }
        serverSocket.close()
    }
}