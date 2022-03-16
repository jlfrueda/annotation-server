package org.clinbioinfosspa.mmp.server.services;

import org.apache.commons.lang3.StringUtils;
import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Service
public class SequenceService {

    @Value("${sequence.cache.path}")
    private String sequenceCachePath;

    private static final int SEQUENCE_CHUNK_SIZE = 32 * 1024;
    private static final String CACHE_NAME = "seqcache";

    private final CacheManager cacheManager = CacheManagerBuilder.newCacheManagerBuilder().withCache(CACHE_NAME, CacheConfigurationBuilder.newCacheConfigurationBuilder(String.class, String.class, ResourcePoolsBuilder.heap(2047)).build()).build(true);
    private final Cache<String, String> sequenceCache = cacheManager.getCache(CACHE_NAME, String.class, String.class);

    public char getReference(String sequenceId, long position) {
        int page = (int)(position / (long)SEQUENCE_CHUNK_SIZE);
        int offset = (int)(position % (long)SEQUENCE_CHUNK_SIZE);
        var cacheId = sequenceId + "|" + Long.toString(page);
        var sequence = sequenceCache.get(sequenceId);
        if (null == sequence)
        {
            sequence = readSequencePage(sequenceId, page);
            sequenceCache.put(sequenceId, sequence);
        }
        return sequence.charAt(offset);
    }

    public String getReference(String sequenceId, long start, long end) {
        var sequence = new StringBuilder((int)(end - start));
        for (long idx = start; idx < end; ++idx) {
            sequence.append(getReference(sequenceId, idx));
        }
        return sequence.toString();
    }

    private String readSequencePage(String sequenceId, int page) {
        var path = Paths.get(sequenceCachePath, sequenceId, Integer.toString(page) + ".data");
        try {
            return Files.readString(path);
        } catch (IOException e) {
            return StringUtils.repeat('N', SEQUENCE_CHUNK_SIZE);
        }
    }
}
