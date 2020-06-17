import com.arangodb.ArangoCollection;
import com.arangodb.ArangoDB;
import com.arangodb.ArangoDatabase;
import com.arangodb.Protocol;
import com.arangodb.entity.BaseDocument;
import com.arangodb.internal.InternalArangoCollection;
import com.arangodb.internal.velocystream.internal.VstConnection;
import com.arangodb.model.DocumentCreateOptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * @author Michele Rastelli
 */
public class InsertBenchmark {

    public static void main(String[] args) {
        ArangoDB arangoDB = new ArangoDB.Builder()
                .host("172.28.3.1", 8529)
                .useProtocol(Protocol.VST)
                .user("root")
                .password("test")
                .build();

        ArangoDatabase db = arangoDB.db("ycsb");
        if (!db.exists())
            db.create();

        ArangoCollection collection = db.collection("myusertable");
        if (collection.exists())
            collection.drop();
        collection.create();

        for (int j = 0; j < 20; j++) {
            VstConnection.elapsedTimes.clear();
            InternalArangoCollection.elapsedTimesInsertDocumentRequestSerialize.clear();
            InternalArangoCollection.elapsedTimesInsertDocumentDeserialize.clear();
            long startTime = System.nanoTime();
            int tot = 100_000;
            for (int i = 0; i < tot; i++) {
                String key = "key-" + UUID.randomUUID().toString();
                BaseDocument doc = new BaseDocument(key);
                doc.addAttribute("field1", "?2z;+x0G')'r?-v./,6A\"',7Qm6?r(3r*X=!>~#6d8M-8Im5T'P5\"\"21Sa?Iy2E?!#n6W3-^5().4Ae+Y; $p#Bi<K#<Z# $\")");
                doc.addAttribute("field0", ":$8!0d/Gw;Gs60f#@0&x3?p1' 54r3=>4C/1>t>A5!6j**\"-T-=Q?,8t:%4%-41R54J50%j3!p\"?x\"V{?-,.$61Gc14f3Gm8K-%");
                doc.addAttribute("field7", "62p;L5=[m>7\"$-j'Nk,,,1B{'Es3=b*\\) ;24>( 7$0&d.240?r6+z$Dk;Ro08x>[q&W14F3?1\"%Dg5,40Qm/-,0W23n608\"3n:");
                doc.addAttribute("field6", "/R%-D%?]!$?h4A+,_'>=h3T'7\\)1Pa0$\"9Bs0Ik;Ei6F'9Zk\"Y\"$j *(5Ii+Z!9Im/Vi' `/0d/4|=Bi2 x K}/Vy)3f/1r)/,<");
                doc.addAttribute("field9", "+S}7Da6%40<l1Pq3E*5:-Su(T/0)r*/49'.(=2(]9+Cu>9p4Ia.Pc/D/5I?:Y9(.\"24*<Q+&?p?(8,+j3@{*>d?=,/_'4P!5).5");
                doc.addAttribute("field8", ".Pw:7n;?:<G+.?*5Vy;824!~+7d;L{$Ri.?&3;&,:t:Q) )h=*$00<1#0%#<*#b?+.\"[1%Va1;f6+>/;6$%604(!_3!/\"%?26+6<");
                doc.addAttribute("field3", "63n;T!*=. @q:Zu;>t+Io2*l=? =Ou&:p?R=,2$4!82&`?3\"!^k>%2+^# 9,)_1-(4'?69H7:F!9]m-&|7<|6&d>Go? p;_q!@3");
                doc.addAttribute("field2", "/Zu;B{;<\"%.641r;#0.Y)$2b&^\",h4X#,8,4*x;(b:%( 6<<S9%%t83~:M9:!4(Kk$K1$)(59j;U3*\\+)^%,^!8]o8+p/&v7X:");
                doc.addAttribute("field5", "5.h2Yu6L) 'r<6~.Gc8Ys\"-`<72-'$79&<@34E=1Qq8#.4He4.,(^g<R.$043v;&z.Ls&#b4A90Vq6'`>502:,)*\"?8&(2,2Pg6");
                doc.addAttribute("field4", "**>!/89%z*#t7=:,.$&_#=H#.B/4Xu,Xc'&4(\\=%7p#E=51p1T?<) 01f27b%Es;8n?]e#3|\"9`.#h+!j\"C#)O#)Qk0#0<%><2(,");

                collection.insertDocument(doc, new DocumentCreateOptions().waitForSync(false));
            }

            long execTime = System.nanoTime() - startTime;
            long avgTime = execTime / tot;

            System.out.println("\n=== === ===");
            System.out.println("--- E2E Latency --- ");
            System.out.println("avg [ns]: " + avgTime);

            ArrayList<Long> elapsedTimesAroundVstConnection = new ArrayList<>(VstConnection.elapsedTimes);
            System.out.println("--- Latency around VstConnection --- ");
            System.out.println("avg [ns]: " + elapsedTimesAroundVstConnection.stream().mapToDouble(o -> o).average().getAsDouble());
            System.out.println("95th [ns]: "+ percentile(elapsedTimesAroundVstConnection, 95));
            System.out.println("99th [ns]: "+ percentile(elapsedTimesAroundVstConnection, 99));
            System.out.println("99.9th [ns]: "+ percentile(elapsedTimesAroundVstConnection, 99.9));

            System.out.println("--- Latency around VPack Serialize --- ");
            System.out.println("avg [ns]: " + InternalArangoCollection.elapsedTimesInsertDocumentRequestSerialize.stream().mapToDouble(o -> o).average().getAsDouble());

            System.out.println("--- Latency around VPack Deserialize --- ");
            System.out.println("avg [ns]: " + InternalArangoCollection.elapsedTimesInsertDocumentDeserialize.stream().mapToDouble(o -> o).average().getAsDouble());

        }

        arangoDB.shutdown();
    }

    private static long percentile(List<Long> values, double percentile) {
        Collections.sort(values);
        int index = (int) Math.ceil((percentile / 100) * values.size());
        return values.get(index - 1);
    }

}
