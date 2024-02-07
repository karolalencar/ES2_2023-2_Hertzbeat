package org.dromara.hertzbeat.manager.service.impl;

import com.obs.services.ObsClient;
import com.obs.services.model.ListObjectsRequest;
import com.obs.services.model.ObjectListing;
import com.obs.services.model.ObsObject;
import com.obs.services.model.PutObjectResult;
import org.dromara.hertzbeat.manager.pojo.dto.FileDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ObsObjectStoreServiceTest {

    @Mock
    ObsClient obsClient = new ObsClient("endpoint");
    @Test
    void uploadSuccessTest() {

        InputStream inputStream = new InputStream() {
            @Override
            public int read() {
                return 0;
            }
        };

        ObsObjectStoreServiceImpl objectStoreService = new ObsObjectStoreServiceImpl(obsClient, "bucketname", "rootpath");

        PutObjectResult result = new PutObjectResult("bucketname", "objectkey", "etag", "versionId", "objectUrl", null, 200);

        when(obsClient.putObject(Mockito.anyString(), Mockito.anyString(), Mockito.any(InputStream.class))).thenReturn(result);
        assertTrue(objectStoreService.upload("/sample/filepath", inputStream), "The upload result should be true");

    }

    @Test
    void downloadSuccessTest() {

        ObsObjectStoreServiceImpl objectStoreService = new ObsObjectStoreServiceImpl(obsClient, "bucketname", "rootpath");

        ObsObject result = new ObsObject();

        when(obsClient.getObject(Mockito.anyString(), Mockito.anyString())).thenReturn(result);
        assertNotNull(objectStoreService.download("filepath"), "The download success result should not be null");
    }

    @Test
    void downloadFailureTest() {

        ObsObjectStoreServiceImpl objectStoreService = new ObsObjectStoreServiceImpl(obsClient, "bucketname", "rootpath");

        when(obsClient.getObject(Mockito.anyString(), Mockito.anyString())).thenThrow(RuntimeException.class);
        assertNull(objectStoreService.download("filepath"), "The download failure result should be null");
    }

    @Test
    void listFileTest() {

        ObsObjectStoreServiceImpl objectStoreService = new ObsObjectStoreServiceImpl(obsClient, "bucketname", "rootpath");

        var builder = new ObjectListing.Builder();

        ObjectListing objectListing = builder.builder();

        when(obsClient.listObjects(any(ListObjectsRequest.class))).thenReturn(objectListing);
        List<FileDTO> filepath = objectStoreService.list("filepath");
        assertEquals(filepath, List.of());
    }

}