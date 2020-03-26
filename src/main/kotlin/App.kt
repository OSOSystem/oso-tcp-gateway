import org.zeromq.SocketType
import org.zeromq.ZContext
import org.zeromq.ZFrame
import org.zeromq.ZMQ

fun main() {
    ZContext().use { context ->
        val stream: ZMQ.Socket = context.createSocket(SocketType.STREAM)
        val forwarder: ZMQ.Socket = context.createSocket(SocketType.REQ)
        stream.bind("tcp://*:8888")
        forwarder.connect( "tcp://*:5556")

        val frame = ZFrame.recvFrame(stream)  // connection id
        stream.recvStr().also { println(it) } // discard empty frame
        stream.recvStr().also { println(it) } // discard message identity which is equal to the connection id
        val payload = stream.recvStr().also { println(it) } // message payload

        forwarder.send("[[<TCP>,<TEXT>]]$payload")
        val reply = forwarder.recvStr()
        forwarder.close()
        println(reply)
    }
}