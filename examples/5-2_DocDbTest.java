package site.hmatsu47.DocDbTest;

import java.util.List;

import com.mysql.cj.xdevapi.Collection;
import com.mysql.cj.xdevapi.DbDoc;
import com.mysql.cj.xdevapi.DocResult;
import com.mysql.cj.xdevapi.Schema;
import com.mysql.cj.xdevapi.Session;
import com.mysql.cj.xdevapi.SessionFactory;


public class Main {

        public static void main(String args[]) {
                // サーバに接続
                Session session = new SessionFactory().getSession("mysqlx://localhost:33060/test_db?user=testuser&password=T35_U53r");

                // DB に接続
                Schema db = session.getSchema("test_db");

                // コレクション'test_collection'を作成
                Collection col = db.createCollection("test_collection", true);

                // コレクションにドキュメントを追加
                col.add("{\"person_id\":1, \"name\":\"⻘⽊\", \"dept\":\"IT 事業部\"}")
                .execute();
                col.add("{\"person_id\":2, \"name\":\"前⽥\", \"dept\":\"コンサル事業部\"}")
                .execute();
                col.add("{\"person_id\":3, \"name\":\"⼭本\", \"dept\":[\"IT 事業部\",\"コンサル事業部\"]}")
                .execute();

                // コレクションの「person_id」列にインデックスを追加
                col.createIndex("pid_index", "{\"fields\": [{\"field\": \"$.person_id\", \"type\": \"INT\"}]}");

                // コレクションから「dept LIKE '%IT事業部%'」を探して表⽰
                searchDept(col, "IT事業部");

                System.out.println();

                // コレクションから「dept LIKE '%コンサル事業部%'」を探して表⽰
                searchDept(col, "コンサル事業部");

                System.out.println();

                // コレクションから「person_id=2」を探して表⽰
                searchPid(col, 2);

                System.out.println();

                // コレクションを削除
                db.dropCollection("test_collection");
        }

        // コレクションから対象ドキュメントの「dept」を⽂字列検索して表⽰する
        private static void searchDept(Collection col, String keyword) {

                System.out.println("Search: " + keyword);
                DocResult docs = col.find("dept like :dept")
                        .bind("dept", "%" + keyword + "%").execute();

                // 結果を取得して表⽰
                List<DbDoc> docl = docs.fetchAll();
                docl.forEach(doc -> System.out.println(doc.toFormattedString()));
        }

        // コレクションから対象ドキュメントの「person_id」を数値検索して表⽰する
        private static void searchPid(Collection col, long value) {

                System.out.println("Search: " + value);
                DocResult docs = col.find("person_id = :pid")
                        .bind("pid", value).execute();

                // 結果を取得して表⽰
                System.out.println(docs.fetchOne().toFormattedString());
        }
}