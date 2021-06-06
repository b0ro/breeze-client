package org.boro.breezeclient.domain

import java.time.Instant

data class PeakFlow(
    var value: Int,
    var checkedAt: Instant,
    var id: Long? = null
) {
    constructor(value: Int, checkedAt: Instant) : this(value, checkedAt, null)
}