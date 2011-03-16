% Filtr Butterwortha
%   >> Hf = butterworth(H, F, Fo, n)
% Oznaczenia:
%   H   - transformata Fouriera
%   F   - widmo cz�stotliwo�ci
%   Fo  - cz�stotliwo�� odci�cia
%   n   - stopie� filtra
function [Hf, Fb] = butterworth(Hp, F, Fo, n)
    Fb = 1 ./ (1 + ((F/Fo).^(2*n)));
    Hno = fftshift(Hp);
    Hf = Hno .* Fb;


