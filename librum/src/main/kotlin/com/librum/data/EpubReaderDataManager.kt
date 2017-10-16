package com.librum.data

import com.librum.data.files.EpubFileHelper
import com.librum.data.prefs.EpubReaderPrefs
import com.librum.data.server.EpubServerHelper

/**
 * @author lusinabrian on 05/09/17.
 * @Notes interface for the data layer of the epub reader lib
 */
interface EpubReaderDataManager : EpubReaderPrefs, EpubFileHelper, EpubServerHelper {
}