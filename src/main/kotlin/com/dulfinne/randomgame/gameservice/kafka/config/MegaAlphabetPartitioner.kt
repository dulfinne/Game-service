package com.dulfinne.randomgame.gameservice.kafka.config

import org.apache.kafka.clients.producer.Partitioner
import org.apache.kafka.common.Cluster

class MegaAlphabetPartitioner : Partitioner {
    override fun configure(p0: MutableMap<String, *>?) {
        // doesn't need full implementation but need these comments
    }

    override fun close() {
        // doesn't need full implementation but need these comments
    }

    override fun partition(
        topic: String?,
        key: Any?,
        keyBytes: ByteArray?,
        value: Any?,
        valueBytes: ByteArray?,
        cluster: Cluster?
    ): Int {
        if (key == null || cluster == null) {
            return 0
        }

        val partitionsCount = cluster.partitionsForTopic(topic).size
        val firstKeyChar = key.toString()
                .uppercase()[0]

        val firstLetter = 'A'
        val lastLetter = 'Z'
        if (firstKeyChar !in firstLetter..lastLetter) return 0

        val lettersPerPartition = (lastLetter - firstLetter + 1).toDouble() / partitionsCount
        val partition = ((firstKeyChar - firstLetter) / lettersPerPartition).toInt()

        return partition
    }
}