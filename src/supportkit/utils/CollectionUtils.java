package supportkit.utils;

import java.util.Collection;

public class CollectionUtils {

	/**
	 * 対象の文字列に対してlistのある要素に対して一つでも部分一致しているかどうか判定します
	 *
	 * @param target 対象の文字列
	 * @param list 検索単語
	 * @return 部分一致していた場合true
	 */
	public static boolean contais(String target, Collection<String> list) {
		for (String element : list) {
			if (target.contains(element)) {
				return true;
			}
		}
		return false;
	}

}
