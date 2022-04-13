package com.example.calypsodivelog.service

import com.example.calypsodivelog.model.DivelogShortListResponse

interface ClickListenerDivelogShortList {
        fun itemSelect(data: DivelogShortListResponse)
}