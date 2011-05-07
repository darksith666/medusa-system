% Funkcja oblicza transformat� Fouriera metod� fft.
% U�ycie:
%   >> [Wf, Wa, H] = fourier(t, h)
% Gdzie:
%   Wf  - widmo cz�stotliwo�ciowe
%   Wa  - widmo amplitudowe
%   H   - transformata pierwotna
function [F, Hr, H] = fourier(t, h)
    % d�ugo�� wektora czasu.
    n = length(t);
    % Obliczamy transformat� Fouriera
    H = fft(h);
    H2 = fftshift(H);                      % transformata i shift
    H2 = 2 * sqrt(H2 .* conj(H2)) / n;     % widmo amplitudy
    zzero = ceil(n / 2);                   % w tym miejscu dzielimy przez 2
    H2(zzero) = H2(zzero) ./ 2;            % aby sk�ad. sta�a by�a zachowana
    % Obliczamy widmo cz�stotliwo�ci
    fs = 1 / (t(2) - t(1));
    f = fs .* [0 : n / 2] ./ n;
    f2 = [-fliplr(f(2 : length(f))) f];
    % zwracamy wyniki
    F = f2;
    Hr = H2;