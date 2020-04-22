package app;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;

@WebServlet("/api/employees")
public class EmployeeServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException {

		// アクセス元のHTMLでｑに設定された値を取得して、String型の変数idに代入
		String id = request.getParameter("q");

		// データベースにアクセスするために、データベースのURLとユーザ名とパスワードを指定します
		// ※SQLのログを出力するため変数urlの値は基本的な形式から少し変更を加えています。
		// 　そのためシステム構築2で使い回すときは注意下さい！
		String url = "jdbc:log4jdbc:oracle:thin:@localhost:1521:XE";
		String user = "wt2";
		String pass = "wt2";

		// エラーが発生するかもしれない処理はtry-catchで囲みます
		// この場合はDBサーバへの接続に失敗する可能性があります
		try (
				// データベースへ接続します
				Connection con = DriverManager.getConnection(url, user, pass);

				// SQLの命令文を実行するための準備をおこないます
				Statement stmt = con.createStatement();

				// SQLの命令文を実行し、その結果をResultSet型のrsに代入します
				ResultSet rs1 = stmt.executeQuery(
						"SELECT syainname, " +
								"	syusinttdfkn, " +
								"	imagefilename, " +
								"	tanjyoymd, "+
								"	daigakuname, " +
								"	senkokamoku, " +
								"	syutokusikaku, " +
								"	nyusyaymd, " +
								"	freecomment " +
								"FROM ms_syain " +
								"WHERE syainid = '" + id + "'");) {

			// 社員情報を保持するため、Employee型の変数empを宣言
			// 変数empはJSPに渡すための社員情報を保持させます
			Employee emp = new Employee();

			// SQL実行結果を保持している変数rsから社員情報を取得
			// rs.nextは取得した社員情報表に次の行があるとき、trueになります
			// 次の行がないときはfalseになります
			if (rs1.next()) {
				emp.setId(id); // 社員IDを変数empに代入
				emp.setName(rs1.getString("syainname"));// SQL実行結果のsyainname列の値を取得し変数empに代入します
				emp.setAddress(rs1.getString("syusinttdfkn")); // SQL実行結果の「syusinttdfkn」列の値を取得し変数empに代入します
				emp.setImage(rs1.getString("imagefilename")); // SQL実行結果の「imagefilename」列の値を取得し変数empに代入します
				emp.setBirthYmd(rs1.getString("tanjyoymd")); // 以下、同様なので以下省略します
				emp.setCollege(rs1.getString("daigakuname"));
				emp.setMajor(rs1.getString("senkokamoku"));
				emp.setLicense(rs1.getString("syutokusikaku"));
				emp.setEnterYmd(rs1.getString("nyusyaymd"));
				emp.setComment(rs1.getString("freecomment"));
			}

			// アクセスした人に応答するためのJSONを用意する
			PrintWriter pw = response.getWriter();

			// JSONで出力する
			pw.append(new ObjectMapper().writeValueAsString(emp));

		} catch (Exception e) {
			throw new RuntimeException(String.format("検索処理の実施中にエラーが発生しました。詳細：[%s]", e.getMessage()), e);
		}
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		doGet(request ,response);
	}

}
