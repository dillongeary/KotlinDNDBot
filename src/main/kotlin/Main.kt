
import dev.kord.core.Kord
import dev.kord.core.event.message.MessageCreateEvent
import dev.kord.core.on
import dev.kord.gateway.Intent
import dev.kord.gateway.PrivilegedIntent
import kotlin.random.Random

suspend fun main() {
    val kord = Kord("ODY0NjI2NDY2MzM0NDQxNDcy.Gap2uD.v9V78Nku0QxTLn9LfGEbq5IBEHbEKbi6EifYYw")

    kord.on<MessageCreateEvent> {
        if (message.author?.isBot != false) return@on
        val reg = Regex("^(([1-9][0-9]?)?[d|D][1-9][0-9]*)([+]([1-9][0-9]?)?[d|D][1-9][0-9]*|[+|-]?[0-9]+)*$")
        if (!reg.containsMatchIn(message.content.replace(" ",""))) return@on
        //message.addReaction(ReactionEmoji.Unicode("game_die"))

        var content = message.content.replace("-","+-")
        var contentList = content.split("+")


        val resultList : List<Triple<Boolean,String,List<Int>>> = contentList.map { command ->
            val commandList = command.split(Regex("d|D"))
            when (commandList.size) {
                1 -> Triple(false,command, List(1) { command.toInt() })
                2 -> Triple(true,command, List(when (commandList.get(0)) {
                    "" -> 1
                    else -> commandList.get(0).toInt()
                }) { Random.nextInt(commandList.get(1).toInt()) + 1})
                else -> throw Error("Incorect Dice Statement")
            }
        }

        val output = resultList.map {
            val isDice = it.first
            val command = it.second
            val result = it.third
            if (!isDice) {
                command
            } else {
                "${command} = ${result.joinToString(" + ")}"
            }
        }

        val total = resultList.sumOf { it.third.sum() }

        val shortMessage = resultList.size == 1 && resultList.get(0).third.size == 1

        val returnMessage = if (shortMessage) {
            "${message.author!!.mention} rolled **${total}**"
        } else {
            "${message.author!!.mention} rolled **${total}**   (${output.joinToString ( ", " )})"
        }

        message.channel.createMessage(returnMessage)
    }

    kord.login {
        presence { playing("Dungeons and Dragons") }
        @OptIn(PrivilegedIntent::class)
        intents += Intent.MessageContent
    }
}