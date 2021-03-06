package org.infinispan.server.hotrod.test;

import java.util.Random;

import org.infinispan.Cache;
import org.infinispan.commons.marshall.Marshaller;
import org.infinispan.commons.marshall.ProtoStreamMarshaller;
import org.infinispan.distribution.LocalizedCacheTopology;

/**
 * {@link org.infinispan.distribution.MagicKey} equivalent for HotRod
 *
 * @author Galder Zamarreño
 * @since 5.2
 */
public class HotRodMagicKeyGenerator {

   public static byte[] newKey(Cache<?, ?> cache) throws Exception {
      LocalizedCacheTopology cacheTopology = cache.getAdvancedCache().getDistributionManager().getCacheTopology();
      Random r = new Random();

      Marshaller marshaller = new ProtoStreamMarshaller();
      for (int i = 0; i < 1000; i++) {
         String candidate = String.valueOf(r.nextLong());
         byte[] candidateBytes = marshaller.objectToByteBuffer(candidate, 64);
         if (cacheTopology.isReadOwner(candidateBytes)) {
            return candidateBytes;
         }
      }

      throw new RuntimeException("Unable to find a key local to node " + cache);
   }
}
