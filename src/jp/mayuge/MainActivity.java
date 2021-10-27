package jp.mayuge;

public class MainActivity {

	// カード一式
	// Note：番号(1-13) * マーク(4種類) + JOKER * 1枚
	private int all_cards[] = null;

	// 掛けるコインの枚数
	private int num_coins = 10;

	/**
	 * 処理を実行する
	 * */
	public void exec() {
		int[] cards = new int[5];

		// ------ ここから編集 ------ //

		// 掛けるコインの枚数を設定する
		this.num_coins = 5;

		// 引いたカードを設定する
		cards[0] = getCardId(6, DQPoker.CARD_MARK_ID_SLIME);
		cards[1] = getCardId(12, DQPoker.CARD_MARK_ID_CROWN);
		cards[2] = getCardId(0);
		cards[3] = getCardId(2, DQPoker.CARD_MARK_ID_SWORD);
		cards[4] = getCardId(2, DQPoker.CARD_MARK_ID_SHIELD);

		// ------ 編集はここまで ------ //

		System.out.println("====== 設定内容 ======");
		System.out.println("掛けるコイン枚数：" + this.num_coins + "枚");
		System.out.println();
		for(int i = 0; i < 5; i++) {
			System.out.println("カード" + (i+1) + "枚目："
				+ DQPoker.CARD_FIGURE_NAMES[cards[i] / 10]
				+ " " + DQPoker.CARD_MARK_NAMES[cards[i] % 10]);
		}

		// 重複するカードがないか判定する
		if(hasDuplicateValues(cards)) {
			System.out.println();
			System.out.println("====== 総合結果 ======");
			System.out.println("カードが重複しています（獲得コイン期待値の計算は行わずに終了）");
			return;
		}

		// 前処理：全てのカードをリストアップする
		this.all_cards = getAllCards();

		// 5枚それぞれのカードを入れ替える場合について、総当たりで期待値を計算する
		System.out.println();
		System.out.println("====== 中間結果 ======");
		boolean flg_exchange[] = new boolean[5];
		double max_ev = 0.0;
		for(int i = 0; i < 2; i++) {
			for(int j = 0; j < 2; j++) {
				for(int k = 0; k < 2; k++) {
					for(int l = 0; l < 2; l++) {
						for(int m = 0; m < 2; m++) {
							// Note：5枚、4枚入れ替えの場合には計算量が膨大、かつ期待値が低い事が多いので、処理対象外にする
							if(i == 1 && j == 1 && k == 1 && l == 1 && m == 1) continue;
							if(i == 1 && j == 1 && k == 1 && l == 1) continue;
							if(i == 1 && j == 1 && k == 1 && m == 1) continue;
							if(i == 1 && j == 1 && l == 1 && m == 1) continue;
							if(i == 1 && k == 1 && l == 1 && m == 1) continue;
							if(j == 1 && k == 1 && l == 1 && m == 1) continue;

							boolean temp_flg_exchange[] = {
								int2bool(i),
								int2bool(j),
								int2bool(k),
								int2bool(l),
								int2bool(m)
							};
							double temp_ev = getExpectedValue(cards, temp_flg_exchange);

							if(temp_ev > max_ev) {
								flg_exchange = temp_flg_exchange;
								max_ev = temp_ev;
							}
							System.out.println("[" + (i == 0 ? " 残" : " - ")
								+ "," + (j == 0 ? " 残" : " - ")
								+ "," + (k == 0 ? " 残" : " - ")
								+ "," + (l == 0 ? " 残" : " - ")
								+ "," + (m == 0 ? " 残" : " - ")
								+ "] " + "獲得コイン期待値：" + Math.round(temp_ev*1000.0)/1000.0
								+ " (損益：" + (temp_ev - this.num_coins < 0 ? "▲" : "＋")
								+ Math.round( Math.abs(temp_ev - this.num_coins) *1000.0 )/1000.0 + "枚)");
						}
					}
				}
			}
		}

		// 結果を取得する
		String result = "";
		for(int i = 0; i < flg_exchange.length; i++) {
			if(!flg_exchange[i]) {
				if(result.length() > 0) {
					result = result + ", ";
				}
				result = result + (i+1) + "枚目";
			}
		}
		System.out.println();
		System.out.println("====== 総合結果 ======");
		result = "残すカード：" + result;
		System.out.println(result);
		System.out.println("獲得コイン期待値：" + Math.round( max_ev*1000.0)/1000.0 + "枚"
			+ " (損益：" + (max_ev - this.num_coins < 0 ? "▲" : "＋")
			+ Math.round( Math.abs(max_ev - this.num_coins) *1000.0 )/1000.0 + "枚)");
	}

	/**
	 * 数字とマークからカード値に変換する
	 * @param figure 数字
	 * @return カード値
	 * */
	private int getCardId(int figure) {
		return getCardId(figure, DQPoker.CARD_MARK_ID_UNKNOWN);
	}


	/**
	 * 数字とマークからカード値に変換する
	 * @param figure 数字
	 * @param mark_id マーク
	 * @return カード値
	 * */
	private int getCardId(int figure, int mark_id) {
		return figure * 10 + mark_id;
	}

	/**
	 * 同じ整数を含んでいるか判定する
	 * @param list [整数]
	 * @return 判定結果
	 * */
	private boolean hasDuplicateValues(int[] list) {
		for(int i = 0; i < list.length; i++) {
			for(int j = 0; j < list.length; j++) {
				if(i == j) continue;

				if(list[i] == list[j]) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 全てのカードをリストアップする
	 * @return [カード値]
	 * */
	private int[] getAllCards() {
		int cards[] = new int[1 + 13*4];

		// 先頭にジョーカーを含める
		cards[0] = 0;
		// 番号×マークを総当たりでリストアップする
		int card_id = 1;
		for(int i = 1; i <= 13; i++) {
			for(int j = 1; j <= 4; j++) {
				cards[card_id] = i * 10 + j;
				card_id++;
			}
		}
		return cards;
	}

	/**
	 * 数値に応じて、true/falseを返す
	 * @param 数値
	 * @return 真偽
	 * */
	private boolean int2bool(int value) {
		return (value > 0);
	}

	/**
	 * 指定したカードを交換した場合の期待値を求める
	 * @param cards [カード]
	 * @param flg_exchange [各カードの交換有無]
	 * @return 期待値
	 * */
	private double getExpectedValue(int[] cards, boolean[] flg_exchange) {
		int raw_ev = 0;
		int num_patterns = 0;
		// 1枚目のカードを決定する
		for(int i = 0; i < this.all_cards.length; i++) {
			// Note：交換するカードの重複、一致を確認する
			if(flg_exchange[0]) {
				if(matchValueInList(cards, this.all_cards[i])) continue;
			}
			else {
				if(this.all_cards[i] != cards[0]) continue;
			}

			// 2枚目のカードを決定する
			for(int j = 0; j < this.all_cards.length; j++) {
				// Note：交換するカードの重複、一致を確認する
				if(i == j) continue;
				if(flg_exchange[1]) {
					if(matchValueInList(cards, this.all_cards[j])) continue;
				}
				else {
					if(this.all_cards[j] != cards[1]) continue;
				}

				// 3枚目のカードを決定する
				for(int k = 0; k < this.all_cards.length; k++) {
					// Note：交換するカードの重複、一致を確認する
					if(i == k || j == k) continue;
					if(flg_exchange[2]) {
						if(matchValueInList(cards, this.all_cards[k])) continue;
					}
					else {
						if(this.all_cards[k] != cards[2]) continue;
					}

					// 4枚目のカードを決定する
					for(int l = 0; l < this.all_cards.length; l++) {
						// Note：交換するカードの重複、一致を確認する
						if(i == l || j == l || k == l) continue;
						if(flg_exchange[3]) {
							if(matchValueInList(cards, this.all_cards[l])) continue;
						}
						else {
							if(this.all_cards[l] != cards[3]) continue;
						}

						// 5枚目のカードを決定する
						for(int m = 0; m < this.all_cards.length; m++) {
							// Note：交換するカードの重複、一致を確認する
							if(i == m || j == m || k == m || l == m) continue;
							if(flg_exchange[4]) {
								if(matchValueInList(cards, this.all_cards[m])) continue;
							}
							else {
								if(this.all_cards[m] != cards[4]) continue;
							}

							int temp_cards[] = {
									this.all_cards[i],
									this.all_cards[j],
									this.all_cards[k],
									this.all_cards[l],
									this.all_cards[m]
							};
							int temp_coin = DQPoker.HAND_RETURN_COINS[getHandId(temp_cards)] * this.num_coins;
							raw_ev += temp_coin;
							num_patterns++;
						}
					}
				}
			}
		}
		double db_raw_ev = raw_ev;
		if(num_patterns == 0) {
			return 0;
		}
		return db_raw_ev / num_patterns;
	}

	/**
	 * 配列の中に、指定した数値と一致するものがあるか確認する
	 * @param list リスト
	 * @param value 確認対象の数値
	 * @return 真偽
	 * */
	private boolean matchValueInList(int[] list, int value) {
		for(int i = 0; i < list.length; i++) {
			if(list[i] == value) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 役の判定をする
	 * @param cards [カード]
	 * @return 役
	 * */
	private int getHandId(int[] cards) {
		int[] figures = getFigures(cards);
		int[] mark_ids = getMarkIds(cards);

		// ロイヤルストレートスライムを判定する
		if(isLSS(figures, mark_ids)) {
			return DQPoker.HAND_ID_LOYAL_STRAIGHT_SLIME;
		}
		// ロイヤルストレートフラッシュを判定する
		if(isLSF(figures, mark_ids)) {
			return DQPoker.HAND_ID_LOYAL_STRAIGHT_FLASH;
		}
		// ファイブカードを判定する
		if(isFiveCards(figures, mark_ids)) {
			return DQPoker.HAND_ID_FIVE_CARDS;
		}
		// ストレートフラッシュを判定する
		if(isSF(figures, mark_ids)) {
			return DQPoker.HAND_ID_STRAIGHT_FLASH;
		}
		// フォーカードを判定する
		if(isFourCards(figures, mark_ids)) {
			return DQPoker.HAND_ID_FOUR_CARDS;
		}
		// フルハウスを判定する
		if(isFH(figures, mark_ids)) {
			return DQPoker.HAND_ID_FULL_HOUSE;
		}
		// フラッシュを判定する
		if(isFlash(figures, mark_ids)) {
			return DQPoker.HAND_ID_FLASH;
		}
		// ストレートを判定する
		if(isStraight(figures, mark_ids)) {
			return DQPoker.HAND_ID_STRAIGHT;
		}
		// スリーカードを判定する
		if(isThreeCards(figures, mark_ids)) {
			return DQPoker.HAND_ID_THREE_CARDS;
		}
		// ツーペアを判定する
		if(isTwoPairs(figures, mark_ids)) {
			return DQPoker.HAND_ID_TWO_PAIR;
		}

		return DQPoker.HAND_ID_UNKNOWN;
	}

	/**
	 * 各カードの数字を取得する
	 * @param cards [カード値]
	 * @return [数字]
	 * */
	private int[] getFigures(int[] cards) {
		int figures[] = new int[cards.length];
		for(int i = 0; i < cards.length; i++) {
			figures[i] = cards[i] / 10;
		}
		return figures;
	}

	/**
	 * 各カードのマークを取得する
	 * @param cards [カード値]
	 * @return [マークID]
	 * */
	private int[] getMarkIds(int[] cards) {
		int mark_ids[] = new int[cards.length];
		for(int i = 0; i < cards.length; i++) {
			mark_ids[i] = cards[i] % 10;
		}
		return mark_ids;
	}

	/**
	 * ロイヤルストレートスライムかどうか判定する
	 * @param figures [数字]
	 * @param mark_ids [マークID]
	 * @return 判定結果
	 * */
	private boolean isLSS(int[] figures, int[] mark_ids) {
		// 同じ数字を含んでいる場合には、ストレートを満たさないので判定対象外とする
		if(hasDuplicateValues(figures)){
			return false;
		}

		int num_card_for_hand = 0;
		for(int i = 0 ; i < figures.length; i++) {

			// ジョーカーはワイルドカード扱いとする
			if(figures[i] == 0) {
				num_card_for_hand++;
			}
			else if(mark_ids[i] == DQPoker.CARD_MARK_ID_SLIME) {
				if( (figures[i] >= 10 && figures[i] <= 13) || figures[i] == 1 ) {
					num_card_for_hand++;
				}
			}
		}
		return (num_card_for_hand == 5);
	}

	/**
	 * ロイヤルストレートフラッシュかどうか判定する
	 * @param figures [数字]
	 * @param mark_ids [マークID]
	 * @return 判定結果
	 * */
	private boolean isLSF(int[] figures, int[] mark_ids) {
		// 同じ数字を含んでいる場合には、ストレートを満たさないので判定対象外とする
		if(hasDuplicateValues(figures)){
			return false;
		}

		// 各マーク別に判定する
		for(int i = 1; i <= 4; i++) {

			// ロイヤルストレートスライムの場合は除外する
			if(i == DQPoker.CARD_MARK_ID_SLIME) continue;

			int num_card_for_hand = 0;

			for(int j = 0 ; j < figures.length; j++) {
				// ジョーカーはワイルドカード扱いとする
				if(figures[j] == 0) {
					num_card_for_hand++;
				}
				else if(mark_ids[j] == i) {
					if( (figures[j] >= 10 && figures[j] <= 13) || figures[j] == 1 ) {
						num_card_for_hand++;
					}
				}
			}
			if (num_card_for_hand == 5) {
				return true;
			}
		}
		return false;
	}

	/**
	 * ファイブカードを判定する
	 * @param figures [数字]
	 * @param mark_ids [マークID]
	 * @return 判定結果
	 * */
	private boolean isFiveCards(int[] figures, int[] mark_ids) {

		// 各番号別に判定する
		for(int i = 1; i <= 13; i++) {

			int num_card_for_hand = 0;

			for(int j = 0; j < figures.length; j++) {
				// ジョーカーはワイルドカード扱いとする
				if(figures[j] == 0) {
					num_card_for_hand++;
				}
				else if(figures[j] == i) {
					num_card_for_hand++;
				}
			}
			if (num_card_for_hand == 5) {
				return true;
			}
		}
		return false;
	}

	/**
	 * ストレートフラッシュを判定する
	 * @param figures [数字]
	 * @param mark_ids [マークID]
	 * @return 判定結果
	 * */
	private boolean isSF(int[] figures, int[] mark_ids) {
		// 同じ数字を含んでいる場合には、ストレートを満たさないので判定対象外とする
		if(hasDuplicateValues(figures)){
			return false;
		}

		// 各番号別に判定する
		// Note：連続する5つの数字に対して、最小の数字を基準に探す（1,2,3,4,5 - 9,10,11,12,13）
		for(int i = 1; i <= 9; i++) {
			// 各マーク別に判定する
			for(int j = 1; j <= 4; j++) {

				int num_card_for_hand = 0;

				for(int k = 0; k < figures.length; k++) {
					// ジョーカーはワイルドカード扱いとする
					if(figures[k] == 0) {
						num_card_for_hand++;
					}
					else if(mark_ids[k] == j) {
						if(i <= figures[k] && figures[k] <= i+4) {
							num_card_for_hand++;
						}
					}
				}
				if (num_card_for_hand == 5) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * フォーカードを判定する
	 * @param figures [数字]
	 * @param mark_ids [マークID]
	 * @return 判定結果
	 * */
	private boolean isFourCards(int[] figures, int[] mark_ids) {
		// 各番号別に判定する
		for(int i = 1; i <= 13; i++) {

			int num_card_for_hand = 0;
			for(int j = 0; j < figures.length; j++) {
				// ジョーカーはワイルドカード扱いとする
				if(figures[j] == 0) {
					num_card_for_hand++;
				}
				else if(figures[j] == i) {
					num_card_for_hand++;
				}
			}
			if (num_card_for_hand == 4) {
				return true;
			}
		}
		return false;
	}

	/**
	 * フルハウスを判定する
	 * @param figures [数字]
	 * @param mark_ids [マークID]
	 * @return 判定結果
	 * */
	private boolean isFH(int[] figures, int[] mark_ids) {
		// 各番号別に判定する
		for(int i = 1; i <= 13; i++) {
			for(int j = 1; j <= 13; j++) {
				if(i == j) continue;

				// まず、同じ数字の札が3枚あるか確認する
				int num_card_for_hand = 0;
				boolean used_card_ids[] = new boolean[figures.length];
				for(int k = 0; k < figures.length; k++) {
					// ジョーカーはワイルドカード扱いとする
					if(figures[k] == 0) {
						num_card_for_hand++;
						used_card_ids[k] = true;
					}
					else if(figures[k] == i) {
						num_card_for_hand++;
						used_card_ids[k] = true;
					}
					else {
						used_card_ids[k] = false;
					}
				}
				if (num_card_for_hand != 3) continue;

				// 次に、同じ数字の札が2枚あるか確認する
				// Note：他の3枚とは異なる数字にする
				num_card_for_hand = 0;
				for(int k = 0; k < figures.length; k++) {
					// Note：既に利用したカードは確認対象に含めない
					if(used_card_ids[k]) continue;

					// ジョーカーはワイルドカード扱いとする
					if(figures[k] == 0) {
						num_card_for_hand++;
					}
					else if(figures[k] == j) {
						num_card_for_hand++;
					}
				}
				if (num_card_for_hand == 2) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * フラッシュを判定する
	 * @param figures [数字]
	 * @param mark_ids [マークID]
	 * @return 判定結果
	 * */
	private boolean isFlash(int[] figures, int[] mark_ids) {
		// マーク別に判定する
		for(int i = 1; i <= 4; i++) {
			int num_card_for_hand = 0;

			for(int j = 0; j < figures.length; j++) {
				// ジョーカーはワイルドカード扱いとする
				if(figures[j] == 0) {
					num_card_for_hand++;
				}
				else if(mark_ids[j] == i) {
					num_card_for_hand++;
				}
			}
			if (num_card_for_hand == 5) {
				return true;
			}
		}
		return false;
	}

	/**
	 * ストレートを判定する
	 * @param figures [数字]
	 * @param mark_ids [マークID]
	 * @return 判定結果
	 * */
	private boolean isStraight(int[] figures, int[] mark_ids) {
		// 同じ数字を含んでいる場合には、ストレートを満たさないので判定対象外とする
		if(hasDuplicateValues(figures)){
			return false;
		}

		// 各番号別に判定する
		// Note：連続する5つの数字に対して、最小の数字を基準に探す（1,2,3,4,5 - 10,11,12,13,1）
		for(int i = 1; i <= 10; i++) {
			int num_card_for_hand = 0;

			for(int j = 0; j < figures.length; j++) {
				// ジョーカーはワイルドカード扱いとする
				if(figures[j] == 0) {
					num_card_for_hand++;
				}
				if(i <= figures[j] && figures[j] <= i+4) {
					num_card_for_hand++;
				}
				// Note：10,11,12,13,1の場合、1を判定できるようにする
				if(i == 10 && figures[j] == 1) {
					num_card_for_hand++;
				}
			}
			if (num_card_for_hand == 5) {
				return true;
			}
		}
		return false;
	}

	/**
	 * スリーカードを判定する
	 * @param figures [数字]
	 * @param mark_ids [マークID]
	 * @return 判定結果
	 * */
	private boolean isThreeCards(int[] figures, int[] mark_ids) {
		// 各番号別に判定する
		for(int i = 1; i <= 13; i++) {

			int num_card_for_hand = 0;
			for(int j = 0; j < figures.length; j++) {
				// ジョーカーはワイルドカード扱いとする
				if(figures[j] == 0) {
					num_card_for_hand++;
				}
				else if(figures[j] == i) {
					num_card_for_hand++;
				}
			}
			if (num_card_for_hand == 3) {
				return true;
			}
		}
		return false;
	}

	/**
	 * ツーペアを判定する
	 * @param figures [数字]
	 * @param mark_ids [マークID]
	 * @return 判定結果
	 * */
	private boolean isTwoPairs(int[] figures, int[] mark_ids) {
		// 各番号別に判定する
		for(int i = 1; i <= 13; i++) {
			for(int j = 1; j <= 13; j++) {
				if(i == j) continue;

				// まず、同じ数字の札が2枚あるか確認する
				int num_card_for_hand = 0;
				boolean used_card_ids[] = new boolean[figures.length];
				for(int k = 0; k < figures.length; k++) {
					// ジョーカーはワイルドカード扱いとする
					if(figures[k] == 0) {
						num_card_for_hand++;
						used_card_ids[k] = true;
					}
					else if(figures[k] == i) {
						num_card_for_hand++;
						used_card_ids[k] = true;
					}
					else {
						used_card_ids[k] = false;
					}
				}
				if (num_card_for_hand != 2) continue;

				// 次に、同じ数字の札が2枚あるか確認する
				// Note：他の3枚とは異なる数字にする
				num_card_for_hand = 0;
				for(int k = 0; k < figures.length; k++) {
					// Note：既に利用したカードは確認対象に含めない
					if(used_card_ids[k]) continue;

					// ジョーカーはワイルドカード扱いとする
					if(figures[k] == 0) {
						num_card_for_hand++;
					}
					else if(figures[k] == j) {
						num_card_for_hand++;
					}
				}
				if (num_card_for_hand == 2) {
					return true;
				}
			}
		}
		return false;
	}
}
