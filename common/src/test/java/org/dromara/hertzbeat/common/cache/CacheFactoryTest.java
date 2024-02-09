package org.dromara.hertzbeat.common.cache;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CacheFactoryTest {

    @Test
    void testGetNoticeCache() {
        ICacheService<String, Object> noticeCache = CacheFactory.getNoticeCache();
        assertNotNull(noticeCache);
        assertTrue(noticeCache instanceof ICacheService);
    }

    @Test
    void testGetAlertSilenceCache() {
        ICacheService<String, Object> alertSilenceCache = CacheFactory.getAlertSilenceCache();
        assertNotNull(alertSilenceCache);
        assertTrue(alertSilenceCache instanceof ICacheService);
    }

    @Test
    void testGetAlertConvergeCache() {
        ICacheService<String, Object> alertConvergeCache = CacheFactory.getAlertConvergeCache();
        assertNotNull(alertConvergeCache);
        assertTrue(alertConvergeCache instanceof ICacheService);
    }
}