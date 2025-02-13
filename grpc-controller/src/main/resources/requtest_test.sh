#!/bin/bash
#set -x

if [ -z "$1" ]; then
    echo "error: gateway ip is empty."
    exit 1
fi

gateway_ip=$1

private_key="-----BEGIN PRIVATE KEY-----
MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQC2kP4xbdcD4kv2
erK9OVdYleV+roPJeY2rHN6Cbjq1pZCCZ2TtIKvMhP3b1vKnwv2E6PF+C+z7QVe0
vr5EDVWR8mEpAbZG97KMfT7U8673d6gxCtV3hWaPehFuGXD2LNb+z8+tHyW3o0c7
+BN8OKAucRP0Y/VyO6N6I9HlAbHnF9ooC/9QHwqRiVz5jDX3Hk6Q1SGmfoEH6I2N
jrGtR4NI4WL1nyge9KSEDjKfeDdGXG4pWGmVC3996h71X5vflQI3C5gx5vOgEFBZ
dzCUyolqBsdjL8zxOH6c6rZDBh/xRWdDmkUiS+XxpxaCG1cJerCEVzNGJixasobL
21WWX8lVAgMBAAECggEAWNJwKYJEIwqdZJzLi05zlZDQJ/MmAQbXixGvnAufGrMj
fJ5rNwc3LnjhvWx1gHNYvqpKm8WXlaJz1mca0KcgI7Kl3LqNgTePsdRZlme8j3+y
37FirBTGEjYUdtl/4PVt4GkgpBzMT+zrglyutjgijpXLXJKpXttvLMRyoRVTBZf8
dMXLqR9GEfLlp+Fq320KjHSQNbFWdiLsMaTueld72kBMrVqdObIWhqr6TjLgWHlY
KDceKyzdiEC1j4xdfH1aHpnp90TAQL/g5lhoBcS9hCURGcWVJMPZigwasmNKhkIV
m4RqV7fvquN38KtDmg4ozAIcLoq5Z5vSOWOQ6E/1HQKBgQDyj8LEIUC40vFDMpPl
4OIj5dbT3kiAucrW17qSHVUwZrjQKT2b7U/oE5nrjBB9NjifFk3bM+7uh7byvGJ3
b5UCQ6TxN9tHW/Tkap8Iy4JIxyoCcC05QYpxP48Rk3jW71mGN+Ec2qtKK0FCk8nV
+iKUiWhaB4sBsqBGxt/CcUr1wwKBgQDArlK/+yGLGdF4U6izYYW0AWpBJIGZggcN
uv+IyUfq9cegJzN7QCQevxvdNsVUsioliFk+bwZ7Mc3dvQ5ZD01BrvJbl6Vv9RHt
Iaoff8OQu6J+cv4W5iXO4o0u8logQ8LaS/ZCDzLHWVc7lyGkWByRiY7tJp/rjhqq
aHpbJQKbBwKBgHiI9fkuqRHvSRLidFpcmSPLmCiqog7NxL1kcIMhbm7h7N8MT2BH
2lhyPR+u95axNgvgMopGUWpZKJGRCcFeQobghHuWDTJ1wMktLqBZOQHfbsHNaru0
FQ7XzcmnJfpw8GThKc2D4HC38/MYq8jYRwzXB3MNoocSc9EYDKN8VwqvAoGBAKMG
W6lnt/fTfnIKhqBPkSq+T8KSOr1BUOqDH5YCh8D6905AHMTZfjYtiEvg/ZEttY9Q
EAJNOfSeLQrB3RPpaHp8pT5tzmItvl8erqw8E6GvwEqwzN9fFvo2oTHph+voqUK+
Jru51ELrCm2275X3nyRcePuaRJCvCUzJXU0kwVGdAoGBAOjcIN7OTdMHKaO/n64l
TtMgUySUYCBXFI35fw8dJRHi1/IL8omAjdJM3tBOCVnvzw0FGpd4AeDGviAQ8DWp
Las96DREPJsYfZzyq3zc7eE0+c/gqx3Tg3Xur42xcZhrErP8Yh+vWtFTMdIukpqc
hsb8n5Wm3GiMNVGNsD/cacoi
-----END PRIVATE KEY-----"

echo "$private_key" > private_key.pem

public_key="-----BEGIN PUBLIC KEY-----
MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAtpD+MW3XA+JL9nqyvTlX
WJXlfq6DyXmNqxzegm46taWQgmdk7SCrzIT929byp8L9hOjxfgvs+0FXtL6+RA1V
kfJhKQG2RveyjH0+1POu93eoMQrVd4Vmj3oRbhlw9izW/s/PrR8lt6NHO/gTfDig
LnET9GP1cjujeiPR5QGx5xfaKAv/UB8KkYlc+Yw19x5OkNUhpn6BB+iNjY6xrUeD
SOFi9Z8oHvSkhA4yn3g3RlxuKVhplQt/feoe9V+b35UCNwuYMebzoBBQWXcwlMqJ
agbHYy/M8Th+nOq2QwYf8UVnQ5pFIkvl8acWghtXCXqwhFczRiYsWrKGy9tVll/J
VQIDAQAB
-----END PUBLIC KEY-----"

echo "$public_key" > public_key.pem

plaintext_request_data='{"name":"tom", "age":18}'
echo "plaintext request data is: ${plaintext_request_data}."

encrypted_request_data=$(echo -n $plaintext_request_data | openssl rsautl -encrypt -pubin -inkey public_key.pem | base64 | tr -d '\n')
echo "encrypted request data is: ${encrypted_request_data}."

response=$(curl -s -X POST --data-binary $encrypted_request_data http://$gateway_ip/ex-processor-provider/hello)
echo "encrypted response data is: ${response}."

plaintext_response_data=$(echo $response | base64 -d | openssl rsautl -decrypt -inkey private_key.pem)
echo "plaintext response data is: ${plaintext_response_data}."