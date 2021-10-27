package jp.mayuge;

/**
 * 役やカードに関するクラス
 * */
public class DQPoker {
	// 役
	public static final int HAND_ID_UNKNOWN = 0;
	public static final int HAND_ID_TWO_PAIR = 1;
	public static final int HAND_ID_THREE_CARDS = 2;
	public static final int HAND_ID_STRAIGHT = 3;
	public static final int HAND_ID_FLASH = 4;
	public static final int HAND_ID_FULL_HOUSE = 5;
	public static final int HAND_ID_FOUR_CARDS = 6;
	public static final int HAND_ID_STRAIGHT_FLASH = 7;
	public static final int HAND_ID_FIVE_CARDS = 8;
	public static final int HAND_ID_LOYAL_STRAIGHT_FLASH = 9;
	public static final int HAND_ID_LOYAL_STRAIGHT_SLIME = 10;

	public static final int[] HAND_RETURN_COINS = {
		0,
		1,
		2,
		4,
		5,
		6,
		10,
		20,
		100,
		250,
		500
	};

	// （カードの）数字
	public static final String[] CARD_FIGURE_NAMES = {
		"JOKER",
		"A",
		"2",
		"3",
		"4",
		"5",
		"6",
		"7",
		"8",
		"9",
		"10",
		"J",
		"Q",
		"K"
	};

	// （カードの）マーク
	public static final int CARD_MARK_ID_UNKNOWN = 0;
	public static final int CARD_MARK_ID_SLIME = 1;
	public static final int CARD_MARK_ID_SWORD = 2;
	public static final int CARD_MARK_ID_CROWN = 3;
	public static final int CARD_MARK_ID_SHIELD = 4;

	public static final String[] CARD_MARK_NAMES = {
		"",
		"スライム",
		"ソード",
		"王冠",
		"シールド"
	};
}