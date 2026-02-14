package pingu.netty

import io.netty.buffer.ByteBuf
import io.netty.channel.Channel
import io.netty.channel.socket.DatagramPacket
import io.netty.util.AttributeKey
import pingu.server.User
import java.net.InetSocketAddress
import javax.management.Query.attr

typealias Client = Channel

/*val UserKey = AttributeKey.valueOf<User>("u")
var Client.user
    get() = attr(UserKey).get()
    set(value) = attr(UserKey).set(value)*/

fun Client.send(vararg packets: PKT) {
    packets.forEach { packet ->
        write(packet)
    }
    flush()
}

infix fun Client.send(packet: PKT) {
    writeAndFlush(packet)
}