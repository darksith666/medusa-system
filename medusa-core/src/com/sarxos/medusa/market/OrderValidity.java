package com.sarxos.medusa.market;

public enum OrderValidity {

	/**
	 * Up to particular date.
	 */
	TO_DATE,

	/**
	 * Zlecenie wa�ne bezterminowo, a wi�c do momentu odwo�ania przez Inwestora
	 * lub anulowania przez gie�d�.
	 */
	DOM,

	/**
	 * Wykonaj lub anuluj - zlecenie jest realizowane natychmiast po wys�aniu,
	 * wy��cznie w ca�o�ci. Zlecenie nie mo�e by� sk�adane w fazie interwencji.
	 * Je�eli w arkuszu zlece� na gie�dzie brak zlece� przeciwstawnych z limitem
	 * ceny umo�liwiaj�cym zawarcie transakcji, zlecenie traci wa�no��. W
	 * przypadku tego typu wa�no�ci nie ma mo�liwo�ci modyfikacji dat wa�no�ci
	 * zlecenia.
	 */
	WuA,

	/**
	 * Wykonaj i anuluj - zlecenie traci wa�no�� po zawarciu pierwszej
	 * transakcji (lub pierwszych transakcji, je�eli zlecenie zostanie
	 * zrealizowane jednocze�nie w kilku transakcjach). Zlecenie jest
	 * realizowane natychmiast po wys�aniu a w przypadku zrealizowania
	 * cz�ciowego, niezrealizowana cz�� traci wa�no��. Zlecenie nie mo�e by�
	 * sk�adane w fazie interwencji. Je�eli w arkuszu zlece� na gie�dzie brak
	 * zlece� przeciwstawnych z limitem ceny umo�liwiaj�cym zawarcie transakcji,
	 * zlecenie traci wa�no��. W przypadku tego typu wa�no�ci nie ma mo�liwo�ci
	 * modyfikacji dat wa�no�ci zlecenia.
	 */
	WiN;

}
