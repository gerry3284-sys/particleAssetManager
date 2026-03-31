package com.particle.asset.manager.controllers;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;

@Controller
public class SwaggerRedirectController {

    @GetMapping("/swagger-ui/index.html")
    public void injectCss(HttpServletResponse response) throws IOException {
        response.setContentType("text/html");
        String html = """
            <!DOCTYPE html>
            <html lang="en">
            <head>
              <meta charset="UTF-8">
              <title>Swagger UI</title>
              <link rel="stylesheet" type="text/css" href="./swagger-ui.css" />
              <link rel="stylesheet" type="text/css" href="index.css" />
              <link rel="stylesheet" type="text/css" href="/swagger-dark.css" />
              <link rel="icon" type="image/png" href="./favicon-32x32.png" sizes="32x32" />
              <link rel="icon" type="image/png" href="./favicon-16x16.png" sizes="16x16" />
            </head>
            <body>
              <div id="swagger-ui"></div>
              <script src="./swagger-ui-bundle.js" charset="UTF-8"></script>
              <script src="./swagger-ui-standalone-preset.js" charset="UTF-8"></script>
              <script src="./swagger-initializer.js" charset="UTF-8"></script>
              <script>
                const microlightObserver = new MutationObserver(() => {

                  document.querySelectorAll('pre.microlight').forEach(pre => {
                    pre.style.setProperty('background', '#1e1e1e', 'important');
                    pre.style.setProperty('padding', '10px', 'important');

                    // Prima parentesi [ o { => bianca
                    const firstSpan = pre.querySelector('span:first-child');
                    if (firstSpan) {
                      const t = firstSpan.textContent.trim();
                      if (t === '[' || t === '{') {
                        firstSpan.style.setProperty('color', '#ffffff', 'important');
                      }
                    }
                    // Anche il testo diretto (nodo testo) prima di qualsiasi span
                    pre.childNodes.forEach(node => {
                      if (node.nodeType === Node.TEXT_NODE) {
                        const t = node.textContent.trim();
                        if (t === '[' || t === '{') {
                          const wrapper = document.createElement('span');
                          wrapper.style.color = '#ffffff';
                          node.parentNode.insertBefore(wrapper, node);
                          wrapper.appendChild(node);
                        }
                      }
                    });
                  });

                  document.querySelectorAll('pre.microlight span').forEach(span => {
                    const color = span.style.color;
                    const text = span.textContent.trim();

                    // Parentesi aperte/chiuse e punteggiatura strutturale => bianco
                    if (!color && (text === '[' || text === ']' || text === '{' || text === '}')) {
                      span.style.setProperty('color', '#ffffff', 'important');
                    }
                    // Stringhe e date => verde
                    else if (color === 'rgb(162, 252, 162)') {
                      span.style.setProperty('color', '#7ec699', 'important');
                    }
                    // Numeri => rosso chiaro
                    else if (color === 'rgb(211, 99, 99)') {
                      span.style.setProperty('color', '#f08080', 'important');
                    }
                    // Booleani => arancione
                    else if (color === 'rgb(252, 194, 140)') {
                      span.style.setProperty('color', '#ce9178', 'important');
                    }
                    // Chiavi, punteggiatura => azzurro chiaro
                    else if (!color) {
                      span.style.setProperty('color', '#9cdcfe', 'important');
                    }
                  });

                  // Pulsante Clear
                  document.querySelectorAll('.btn-clear, .btn.clear').forEach(btn => {
                      btn.style.setProperty('color', '#ff6b6b', 'important');
                      btn.style.setProperty('border-color', '#ff6b6b', 'important');
                      btn.style.setProperty('background', 'transparent', 'important');
                      btn.style.setProperty('font-weight', 'bold', 'important');
                      btn.style.setProperty('opacity', '1', 'important');
                  });
                });

                microlightObserver.observe(document.body, { childList: true, subtree: true });
              </script>
            </body>
            </html>
           """;
        response.getWriter().write(html);
    }
}