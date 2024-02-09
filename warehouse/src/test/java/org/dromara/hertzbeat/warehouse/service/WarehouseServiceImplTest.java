package org.dromara.hertzbeat.warehouse.service;

import org.dromara.hertzbeat.common.entity.message.CollectRep;
import org.dromara.hertzbeat.warehouse.store.AbstractRealTimeDataStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class WarehouseServiceImplTest {

    private WarehouseServiceImpl warehouseService;

    @Mock
    private AbstractRealTimeDataStorage realTimeDataStorage1;

    @Mock
    private AbstractRealTimeDataStorage realTimeDataStorage2;

    @SuppressWarnings("deprecation")
    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        List<AbstractRealTimeDataStorage> dataStorages = new ArrayList<>();
        dataStorages.add(realTimeDataStorage1);
        dataStorages.add(realTimeDataStorage2);
        warehouseService = new WarehouseServiceImpl(dataStorages);
    }

    @Test
    void queryMonitorMetricsDataTest_Success() {
        long monitorId = 123L;
        CollectRep.MetricsData metricsData = new CollectRep.MetricsData();
        List<CollectRep.MetricsData> expectedMetricsData = Collections.singletonList(metricsData);

        when(realTimeDataStorage1.isServerAvailable()).thenReturn(true);
        when(realTimeDataStorage2.isServerAvailable()).thenReturn(true);
        when(realTimeDataStorage1.getCurrentMetricsData(monitorId)).thenReturn(expectedMetricsData);
        when(realTimeDataStorage2.getCurrentMetricsData(monitorId)).thenReturn(expectedMetricsData);

        List<CollectRep.MetricsData> result = warehouseService.queryMonitorMetricsData(monitorId);

        assertEquals(expectedMetricsData, result);
    }

    @Test
    void queryMonitorMetricsDataTest_NoAvailableStorage() {
        long monitorId = 123L;

        when(realTimeDataStorage1.isServerAvailable()).thenReturn(false);
        when(realTimeDataStorage2.isServerAvailable()).thenReturn(false);

        List<CollectRep.MetricsData> result = warehouseService.queryMonitorMetricsData(monitorId);

        assertEquals(Collections.emptyList(), result);
    }
}
