% Filtrowanie widma sygna�u poprzez filtr Wienera.
% Uzycie:
%   >> [FouPrzef, FunTrans, Per] = wiener(Four, maks);
% gdzie:
%   FourPrzef   - przefiltrowana transformata Fouriera sygna�u
%   FunTrans    - funkcja transmisji filtru Wienera
%   Per         - uzyskany w filtrze periodogram
%   Four        - transformata Fouriera sygna�u kty�r� chcemy filtrowa�
%   maks        - do jakiej czesci warto�ci maksymalnej amplitudy ma filtrowa�
% Przyk�adowo je�li max amplituda periodogramu to 200 i wstawimy jako 
% argument maks warto�� 0.01 to wtedy obetnie wszystkie cz�stotliwo�ci, dla
% kt�rych amplituda mocy widoczna w periodogramie jest nie wi�ksza ni� 2,
% czyli warto�� 200 * 0.01 = 2.

function [Hfw, Ft, Per] = wiener(Hp, maks)
    
    n = length(Hp);     % d�ugo�� transformaty
    n2 = ceil(n/2);     % po�owa d�ugo�ci transformaty
    wsp = (1/(n^2));    % wsp�czynnik do mno�enia
    
    Per = zeros(1, n2);         % Periodogram na pocz�tku jako zera.
    Habs2 = abs(Hp).^2;         % Kwadrat modu�u transformaty Fouriera.
    Per(1) = wsp * Habs2(n2);   % Dla pierwszego elementu tylko jeden element z Fouriera
    
    k = 2;     % index dla periodogramu
    
    % Tworzymy periodogram pocz�wszy od drugiego elementu.
    for j = 1 : n2 - 1;
        Per(k) = wsp * (Habs2(j) + Habs2(2 * n2 - j));  % periodogram(k)
        k = k + 1;                                      % zwiekszamy indeks periodogramu
    end

    % testy
    %wx = 1 : length(Per);
    %wpof = polyfit(wx(20 : end), Per(20 : end), 1);
    %wpov = abs(polyval(wpof, wx));
    %plot(wx, Per, wx, wpov);
    
    N = zeros(1, n2);           % wektor szum�w
    maks = maks * max(Per);     % amplituda odci�cia
    
    for j = 1 : n2              % Przechodzimy ca�y periodogram przez p�tl� for
        if Per(j) > maks        % i jesli periodogram wi�kszy od amplitudy odci�cia
            N(j) = maks;           % to szum�w nie ma
        else                    % inaczej
            N(j) = Per(j);      % wstawiamy wartosc periodogramu czyli w tym miejscu wartosc szumu
        end
    end
    
    %plot(Per)
    
    HabsZ = Per - N;            % teoretyczna transformata zawuieraj�ca tylko sygna� (bez szum�w bo je
                                % odj�li�my), jednak zawsze w tym sygnale b�d� te� szumy.

    Ft = HabsZ ./ (HabsZ + N);          % Obliczamy funkcje transmisji dla filtru Wienera.
    Ft = [fliplr(Ft), Ft(2 : end)];     % ��czymy po�ow� funkcji transmisji z jej lustrzanym odbiciem

    Hfw = fftshift(Hp) .* Ft;           % mno�ymy transformat� przez funkcje transmisji. To juz jest
                                        % przefiltyrowany sygna�.

