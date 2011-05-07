% Funkcja obliczaj�ca SNR (Signal - Noise Reduction) dla zadanego sygna�u.
% Jako argumenty podajemy w�a�nie sygna� i liczb� okreslaj�c� ilo��
% przedzia��w w kt�rcy liczyc bedziemy SNR.
%
% U�ycie:
%   wsp_snr = snr(sygnal, n_podzial);
% Gdzie:
%   sygnal      - badany sygna� (wektor)
%   n_podzial   - ilo�� odcink�w w kt�rej badamy SNR
function histogram_snr = snr(s, n)

    skok = floor(length(s) / n);
    koniec = skok * floor(length(s) / skok);
    
    k = 1; 
    for j = skok : skok : koniec
        % ma�y zakres w jakim obliczymy SNR
        zakres = [j - skok + 1 : j];
        histogram_snr(k) = max(s) ./ std(detrend(s(zakres)));
        k = k + 1;
    end
    
    
    
