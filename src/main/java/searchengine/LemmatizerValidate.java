package searchengine;

import searchengine.services.impl.LemmaFinder;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LemmatizerValidate {
    public static void main(String[] args) throws IOException {
        LemmaFinder lemmaFinder = new LemmaFinder();
        String text = "Кавказа Повторное появление леопарда в Осетии Кавказе позволяет предположить,\n" +
                " что леопард постоянно обитает в некоторых районах Северного" +
                " Кавказу";
//        HashMap<String, Integer> lemmas = lemmaFinder.findAllLemmas(text);
//        for (Map.Entry<String, Integer> entry : lemmas.entrySet()) {
//            System.out.println(entry);
//        }
        String withoutTags = lemmaFinder.deleteTags("<head>\n" +
                "  <meta charset=\"utf-8\">\n" +
                "  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\n" +
                "  <meta name=\"description\" content=\"Иногда требуется скопировать файл из одной ветки или из другого коммита. Зачем? Ну например обновили конфиг и надо у себя в ветке тоже его использовать без слияний и прочих лишних действий.\">\n" +
                "  <title>Как скопировать файл из другой Git ветки</title>\n" +
                "  <link rel=\"canonical\" href=\"/posts/copy-file-between-git-branches/\">\n" +
                "  <link rel=\"stylesheet\" href=\"/scss/style.min.2cfbe5452aa68da22af4b1f372c76e9843557a47126725b12b90ae2efb533d60.css\">\n" +
                "  <meta property=\"og:title\" content=\"Как скопировать файл из другой Git ветки\">\n" +
                "  <meta property=\"og:description\" content=\"Иногда требуется скопировать файл из одной ветки или из другого коммита. Зачем? Ну например обновили конфиг и надо у себя в ветке тоже его использовать без слияний и прочих лишних действий.\">\n" +
                "  <meta property=\"og:url\" content=\"/posts/copy-file-between-git-branches/\">\n" +
                "  <meta property=\"og:site_name\" content=\"Konstantin Shibkov\">\n" +
                "  <meta property=\"og:type\" content=\"article\">\n" +
                "  <meta property=\"article:section\" content=\"Posts\">\n" +
                "  <meta property=\"article:tag\" content=\"git\">\n" +
                "  <meta property=\"article:published_time\" content=\"2023-01-20T17:28:25+05:00\">\n" +
                "  <meta property=\"article:modified_time\" content=\"2023-01-20T17:28:25+05:00\">\n" +
                "  <meta name=\"twitter:title\" content=\"Как скопировать файл из другой Git ветки\">\n" +
                "  <meta name=\"twitter:description\" content=\"Иногда требуется скопировать файл из одной ветки или из другого коммита. Зачем? Ну например обновили конфиг и надо у себя в ветке тоже его использовать без слияний и прочих лишних действий.\">\n" +
                " </head><body class=\"\n" +
                "     article-page\n" +
                "     \">\n" +
                "  <script>\n" +
                "         (function() {\n" +
                "             const colorSchemeKey = 'StackColorScheme';\n" +
                "             if(!localStorage.getItem(colorSchemeKey)){\n" +
                "                 localStorage.setItem(colorSchemeKey, \"auto\");\n" +
                "             }\n" +
                "         })();\n" +
                "     </script>\n" +
                "  <script>\n" +
                "     (function() {\n" +
                "         const colorSchemeKey = 'StackColorScheme';\n" +
                "         const colorSchemeItem = localStorage.getItem(colorSchemeKey);\n" +
                "         const supportDarkMode = window.matchMedia('(prefers-color-scheme: dark)').matches === true;\n" +
                " \n" +
                "         if (colorSchemeItem == 'dark' || colorSchemeItem === 'auto' && supportDarkMode) {\n" +
                "             \n" +
                " \n" +
                "             document.documentElement.dataset.scheme = 'dark';\n" +
                "         } else {\n" +
                "             document.documentElement.dataset.scheme = 'light';\n" +
                "         }\n" +
                "     })();\n" +
                " </script>\n" +
                "  <div class=\"container main-container flex on-phone--column extended\">\n" +
                "   <aside class=\"sidebar left-sidebar sticky compact\">\n" +
                "    <button class=\"hamburger hamburger--spin\" type=\"button\" id=\"toggle-menu\" aria-label=\"Показать/скрыть меню\"> <span class=\"hamburger-box\"> <span class=\"hamburger-inner\"></span> </span> </button>\n" +
                "    <header>\n" +
                "     <figure class=\"site-avatar\">\n" +
                "      <a href=\"/\"> <img src=\"/img/ava_hu0e8237203507294e5d179ebaebf784e1_26367_300x0_resize_q75_box.jpg\" width=\"300\" height=\"300\" class=\"site-logo\" loading=\"lazy\" alt=\"Avatar\"> </a>\n" +
                "     </figure>\n" +
                "     <div class=\"site-meta\">\n" +
                "      <h1 class=\"site-name\"><a href=\"/\">Konstantin Shibkov</a></h1>\n" +
                "      <h2 class=\"site-description\"></h2>\n" +
                "     </div>\n" +
                "    </header>\n" +
                "    <ol class=\"menu\" id=\"main-menu\">\n" +
                "     <li><a href=\"/about\">\n" +
                "       <svg xmlns=\"http://www.w3.org/2000/svg\" class=\"icon icon-tabler icon-tabler-mood-smile\" width=\"24\" height=\"24\" viewBox=\"0 0 24 24\" stroke-width=\"2\" stroke=\"currentColor\" fill=\"none\" stroke-linecap=\"round\" stroke-linejoin=\"round\">\n" +
                "        <path stroke=\"none\" d=\"M0 0h24v24H0z\" fill=\"none\" /><path d=\"M12 12m-9 0a9 9 0 1 0 18 0a9 9 0 1 0 -18 0\" /><path d=\"M9 10l.01 0\" /><path d=\"M15 10l.01 0\" /><path d=\"M9.5 15a3.5 3.5 0 0 0 5 0\" />\n" +
                "       </svg><span>Обо мне</span> </a></li>\n" +
                "     <li><a href=\"/books\">\n" +
                "       <svg xmlns=\"http://www.w3.org/2000/svg\" class=\"icon icon-tabler icon-tabler-book-2\" width=\"24\" height=\"24\" viewBox=\"0 0 24 24\" stroke-width=\"2\" stroke=\"currentColor\" fill=\"none\" stroke-linecap=\"round\" stroke-linejoin=\"round\">\n" +
                "        <path stroke=\"none\" d=\"M0 0h24v24H0z\" fill=\"none\" /><path d=\"M19 4v16h-12a2 2 0 0 1 -2 -2v-12a2 2 0 0 1 2 -2h12z\" /><path d=\"M19 16h-12a2 2 0 0 0 -2 2\" /><path d=\"M9 8h6\" />\n" +
                "       </svg><span>Kниги</span> </a></li>\n" +
                "     <li><a href=\"/shorts\">\n" +
                "       <svg xmlns=\"http://www.w3.org/2000/svg\" class=\"icon icon-tabler icon-tabler-ice-cream\" width=\"24\" height=\"24\" viewBox=\"0 0 24 24\" stroke-width=\"2\" stroke=\"currentColor\" fill=\"none\" stroke-linecap=\"round\" stroke-linejoin=\"round\">\n" +
                "        <path stroke=\"none\" d=\"M0 0h24v24H0z\" fill=\"none\" /><path d=\"M12 21.5v-4.5\" /><path d=\"M8 17h8v-10a4 4 0 1 0 -8 0v10z\" /><path d=\"M8 10.5l8 -3.5\" /><path d=\"M8 14.5l8 -3.5\" />\n" +
                "       </svg><span>Shorts</span> </a></li>\n" +
                "     <div class=\"menu-bottom-section\">\n" +
                "      <li id=\"dark-mode-toggle\">\n" +
                "       <svg xmlns=\"http://www.w3.org/2000/svg\" class=\"icon icon-tabler icon-tabler-toggle-left\" width=\"24\" height=\"24\" viewBox=\"0 0 24 24\" stroke-width=\"2\" stroke=\"currentColor\" fill=\"none\" stroke-linecap=\"round\" stroke-linejoin=\"round\">\n" +
                "        <path stroke=\"none\" d=\"M0 0h24v24H0z\" /> <circle cx=\"8\" cy=\"12\" r=\"2\" /> <rect x=\"2\" y=\"6\" width=\"20\" height=\"12\" rx=\"6\" />\n" +
                "       </svg>\n" +
                "       <svg xmlns=\"http://www.w3.org/2000/svg\" class=\"icon icon-tabler icon-tabler-toggle-right\" width=\"24\" height=\"24\" viewBox=\"0 0 24 24\" stroke-width=\"2\" stroke=\"currentColor\" fill=\"none\" stroke-linecap=\"round\" stroke-linejoin=\"round\">\n" +
                "        <path stroke=\"none\" d=\"M0 0h24v24H0z\" /> <circle cx=\"16\" cy=\"12\" r=\"2\" /> <rect x=\"2\" y=\"6\" width=\"20\" height=\"12\" rx=\"6\" />\n" +
                "       </svg><span>Тёмный режим</span></li>\n" +
                "     </div>\n" +
                "    </ol>\n" +
                "   </aside>\n" +
                "   <aside class=\"sidebar right-sidebar sticky\">\n" +
                "    <section class=\"widget archives\">\n" +
                "     <div class=\"widget-icon\">\n" +
                "      <svg xmlns=\"http://www.w3.org/2000/svg\" class=\"icon icon-tabler icon-tabler-hash\" width=\"24\" height=\"24\" viewBox=\"0 0 24 24\" stroke-width=\"2\" stroke=\"currentColor\" fill=\"none\" stroke-linecap=\"round\" stroke-linejoin=\"round\">\n" +
                "       <path stroke=\"none\" d=\"M0 0h24v24H0z\" /> <line x1=\"5\" y1=\"9\" x2=\"19\" y2=\"9\" /> <line x1=\"5\" y1=\"15\" x2=\"19\" y2=\"15\" /> <line x1=\"11\" y1=\"4\" x2=\"7\" y2=\"20\" /> <line x1=\"17\" y1=\"4\" x2=\"13\" y2=\"20\" />\n" +
                "      </svg>\n" +
                "     </div>\n" +
                "     <h2 class=\"widget-title section-title\">Содержание</h2>\n" +
                "     <div class=\"widget--toc\">\n" +
                "      <nav id=\"TableOfContents\">\n" +
                "       <ul>\n" +
                "        <li><a href=\"#кратко\">Кратко</a></li>\n" +
                "        <li><a href=\"#как-это-работает\">Как это работает</a></li>\n" +
                "        <li><a href=\"#а-что-еще-есть\">А что еще есть?</a></li>\n" +
                "       </ul>\n" +
                "      </nav>\n" +
                "     </div>\n" +
                "    </section>\n" +
                "   </aside>\n" +
                "   <main class=\"main full-width\">\n" +
                "    <article class=\"main-article\">\n" +
                "     <header class=\"article-header\">\n" +
                "      <div class=\"article-details\">\n" +
                "       <header class=\"article-category\">\n" +
                "       </header>\n" +
                "       <div class=\"article-title-wrapper\">\n" +
                "        <h2 class=\"article-title\"><a href=\"/posts/copy-file-between-git-branches/\">Как скопировать файл из другой Git ветки</a></h2>\n" +
                "       </div>\n" +
                "       <footer class=\"article-time\">\n" +
                "        <div>\n" +
                "         <svg xmlns=\"http://www.w3.org/2000/svg\" class=\"icon icon-tabler icon-tabler-calendar-time\" width=\"56\" height=\"56\" viewBox=\"0 0 24 24\" stroke-width=\"2\" stroke=\"currentColor\" fill=\"none\" stroke-linecap=\"round\" stroke-linejoin=\"round\">\n" +
                "          <path stroke=\"none\" d=\"M0 0h24v24H0z\" /> <path d=\"M11.795 21h-6.795a2 2 0 0 1 -2 -2v-12a2 2 0 0 1 2 -2h12a2 2 0 0 1 2 2v4\" /> <circle cx=\"18\" cy=\"18\" r=\"4\" /> <path d=\"M15 3v4\" /> <path d=\"M7 3v4\" /> <path d=\"M3 11h16\" /> <path d=\"M18 16.496v1.504l1 1\" />\n" +
                "         </svg><time class=\"article-time--published\">Jan 20, 2023</time>\n" +
                "        </div>\n" +
                "        <div>\n" +
                "         <svg xmlns=\"http://www.w3.org/2000/svg\" class=\"icon icon-tabler icon-tabler-clock\" width=\"24\" height=\"24\" viewBox=\"0 0 24 24\" stroke-width=\"2\" stroke=\"currentColor\" fill=\"none\" stroke-linecap=\"round\" stroke-linejoin=\"round\">\n" +
                "          <path stroke=\"none\" d=\"M0 0h24v24H0z\" /> <circle cx=\"12\" cy=\"12\" r=\"9\" /> <polyline points=\"12 7 12 12 15 15\" />\n" +
                "         </svg><time class=\"article-time--reading\"> Время чтения: 3 мин. </time>\n" +
                "        </div>\n" +
                "       </footer>\n" +
                "      </div>\n" +
                "     </header>\n" +
                "     <section class=\"article-content\">\n" +
                "      <p>Иногда требуется скопировать файл из одной ветки или из другого коммита. Зачем? Ну например обновили конфиг и надо у себя в ветке тоже его использовать без слияний и прочих лишних действий.</p>\n" +
                "      <h2 id=\"кратко\">Кратко</h2>\n" +
                "      <div class=\"highlight\">\n" +
                "       <pre tabindex=\"0\" style=\"color:#f8f8f2;background-color:#272822;-moz-tab-size:4;-o-tab-size:4;tab-size:4;\"><code class=\"language-bash\" data-lang=\"bash\"><span style=\"display:flex;\"><span>git checkout &lt;from-branch-name&gt; &lt;path-to-file-or-dir&gt;\n" +
                " </span></span></code></pre>\n" +
                "      </div>\n" +
                "      <h2 id=\"как-это-работает\">Как это работает</h2>\n" +
                "      <p>Если хотите повторить все действия, сделайте простой репозиторий с двумя ветками и в ветке <code>second</code>&nbsp; будет файл в директории. Порядок команд для этого:</p>\n" +
                "      <div class=\"highlight\">\n" +
                "       <pre tabindex=\"0\" style=\"color:#f8f8f2;background-color:#272822;-moz-tab-size:4;-o-tab-size:4;tab-size:4;\"><code class=\"language-bash\" data-lang=\"bash\"><span style=\"display:flex;\"><span>git init -b master<span style=\"color:#f92672\">&amp;&amp;</span> <span style=\"color:#ae81ff\">\\\n" +
                " </span></span></span><span style=\"display:flex;\"><span><span style=\"color:#ae81ff\"></span>echo <span style=\"color:#e6db74\">\"this is txt file\"</span> &gt; main.txt <span style=\"color:#f92672\">&amp;&amp;</span> <span style=\"color:#ae81ff\">\\\n" +
                " </span></span></span><span style=\"display:flex;\"><span><span style=\"color:#ae81ff\"></span>git add main.txt <span style=\"color:#f92672\">&amp;&amp;</span> <span style=\"color:#ae81ff\">\\\n" +
                " </span></span></span><span style=\"display:flex;\"><span><span style=\"color:#ae81ff\"></span>git commit -m <span style=\"color:#e6db74\">\"Initial commit\"</span> <span style=\"color:#f92672\">&amp;&amp;</span> <span style=\"color:#ae81ff\">\\\n" +
                " </span></span></span><span style=\"display:flex;\"><span><span style=\"color:#ae81ff\"></span>git checkout -b second <span style=\"color:#f92672\">&amp;&amp;</span> <span style=\"color:#ae81ff\">\\\n" +
                " </span></span></span><span style=\"display:flex;\"><span><span style=\"color:#ae81ff\"></span>mkdir myDir <span style=\"color:#f92672\">&amp;&amp;</span> <span style=\"color:#ae81ff\">\\\n" +
                " </span></span></span><span style=\"display:flex;\"><span><span style=\"color:#ae81ff\"></span>echo <span style=\"color:#e6db74\">\"must be copied\"</span> &gt; myDir/second.txt <span style=\"color:#f92672\">&amp;&amp;</span> <span style=\"color:#ae81ff\">\\\n" +
                " </span></span></span><span style=\"display:flex;\"><span><span style=\"color:#ae81ff\"></span>git add myDir/second.txt <span style=\"color:#f92672\">&amp;&amp;</span> <span style=\"color:#ae81ff\">\\\n" +
                " </span></span></span><span style=\"display:flex;\"><span><span style=\"color:#ae81ff\"></span>git commit -m <span style=\"color:#e6db74\">\"added second.txt\"</span>\n" +
                " </span></span></code></pre>\n" +
                "      </div>\n" +
                "      <p>Вы можете скопировать и выполнить все команды за один раз.</p>\n" +
                "      <p>Допустим, нам требуется скопировать файл <code>myDir/second.txt</code> из ветки <code>second</code> в <code>master</code>. Для копирования файла, перейдите в ветку, в которую хотите скопировать файл или директорию из другой ветки.</p>\n" +
                "      <div class=\"highlight\">\n" +
                "       <pre tabindex=\"0\" style=\"color:#f8f8f2;background-color:#272822;-moz-tab-size:4;-o-tab-size:4;tab-size:4;\"><code class=\"language-bash\" data-lang=\"bash\"><span style=\"display:flex;\"><span>git checkout master\n" +
                " </span></span></code></pre>\n" +
                "      </div>\n" +
                "      <p>После можно выполнить команду копирования:</p>\n" +
                "      <div class=\"highlight\">\n" +
                "       <pre tabindex=\"0\" style=\"color:#f8f8f2;background-color:#272822;-moz-tab-size:4;-o-tab-size:4;tab-size:4;\"><code class=\"language-bash\" data-lang=\"bash\"><span style=\"display:flex;\"><span>git checkout second ./myDir/second.txt\n" +
                " </span></span></code></pre>\n" +
                "      </div>\n" +
                "      <p>После этого из ветки <code>second</code> будет скопирован файл, при этом сохранится путь до файла. То есть в <code>master</code> будет лежать файл в папке <code>mydDir</code>.</p>\n" +
                "      <blockquote>\n" +
                "       <p>Если файл уже есть в текущей ветке - он будет перезаписан!</p>\n" +
                "      </blockquote>\n" +
                "      <p>Давайте посмотрим и убедимся - файл у нас скопировался:</p>\n" +
                "      <div class=\"highlight\">\n" +
                "       <pre tabindex=\"0\" style=\"color:#f8f8f2;background-color:#272822;-moz-tab-size:4;-o-tab-size:4;tab-size:4;\"><code class=\"language-bash\" data-lang=\"bash\"><span style=\"display:flex;\"><span>git status\n" +
                " </span></span><span style=\"display:flex;\"><span>\n" +
                " </span></span><span style=\"display:flex;\"><span>On branch master\n" +
                " </span></span><span style=\"display:flex;\"><span>Changes to be committed:\n" +
                " </span></span><span style=\"display:flex;\"><span>  <span style=\"color:#f92672\">(</span>use <span style=\"color:#e6db74\">\"git restore --staged &lt;file&gt;...\"</span> to unstage<span style=\"color:#f92672\">)</span>\n" +
                " </span></span><span style=\"display:flex;\"><span>\tnew file:   myDir/second.txt\n" +
                " </span></span></code></pre>\n" +
                "      </div>\n" +
                "      <p>При этом, файл уже подготовлен к коммиту.</p>\n" +
                "      <blockquote>\n" +
                "       <p>Если вам нужно из конкретного коммита скопировать файл, просто замените название ветки на хэш коммита.</p>\n" +
                "      </blockquote>\n" +
                "      <h2 id=\"а-что-еще-есть\">А что еще есть?</h2>\n" +
                "      <p>А если мы хотим скопировать файл по другому пути? Так тоже можно, для этого можно использовать другую команду <code>git show</code>.</p>\n" +
                "      <p>Чтобы повторить действия - удалите директорию и снова создайте репозиторий с двумя ветками, использую код выше.</p>\n" +
                "      <p>И давайте перейдем в <code>master</code> и создадим папку <code>config</code> и в нее позже скопируем файл <code>second.txt</code>.</p>\n" +
                "      <div class=\"highlight\">\n" +
                "       <pre tabindex=\"0\" style=\"color:#f8f8f2;background-color:#272822;-moz-tab-size:4;-o-tab-size:4;tab-size:4;\"><code class=\"language-bash\" data-lang=\"bash\"><span style=\"display:flex;\"><span>git checkout master\n" +
                " </span></span><span style=\"display:flex;\"><span>mkdir config\n" +
                " </span></span></code></pre>\n" +
                "      </div>\n" +
                "      <p>И самое время выполнить команду вида:</p>\n" +
                "      <div class=\"highlight\">\n" +
                "       <pre tabindex=\"0\" style=\"color:#f8f8f2;background-color:#272822;-moz-tab-size:4;-o-tab-size:4;tab-size:4;\"><code class=\"language-bash\" data-lang=\"bash\"><span style=\"display:flex;\"><span>git show &lt;branch-name-or-hash&gt;:&lt;path-to-copy&gt; &gt; &lt;path-to-paste&gt;\n" +
                " </span></span></code></pre>\n" +
                "      </div>\n" +
                "      <p>указываем ветку или хэш коммита, далее источник файла для копирования и путь до нового файла в текущей ветке.</p>\n" +
                "      <blockquote>\n" +
                "       <p>Это немного хак, так как команда show “показывает” файл из другой ветки. А мы этот вывод пишем в новый файл у себя, для этого и указываем вывод через &gt;</p>\n" +
                "      </blockquote>\n" +
                "      <p>В нашем случае будет так:</p>\n" +
                "      <div class=\"highlight\">\n" +
                "       <pre tabindex=\"0\" style=\"color:#f8f8f2;background-color:#272822;-moz-tab-size:4;-o-tab-size:4;tab-size:4;\"><code class=\"language-bash\" data-lang=\"bash\"><span style=\"display:flex;\"><span>git show second:myDir/second.txt &gt; config/config.txt\n" +
                " </span></span></code></pre>\n" +
                "      </div>\n" +
                "      <p>Убедимся что файл скопирован:</p>\n" +
                "      <div class=\"highlight\">\n" +
                "       <pre tabindex=\"0\" style=\"color:#f8f8f2;background-color:#272822;-moz-tab-size:4;-o-tab-size:4;tab-size:4;\"><code class=\"language-bash\" data-lang=\"bash\"><span style=\"display:flex;\"><span>git status -uall\n" +
                " </span></span><span style=\"display:flex;\"><span>\n" +
                " </span></span><span style=\"display:flex;\"><span>On branch master\n" +
                " </span></span><span style=\"display:flex;\"><span>Untracked files:\n" +
                " </span></span><span style=\"display:flex;\"><span>  <span style=\"color:#f92672\">(</span>use <span style=\"color:#e6db74\">\"git add &lt;file&gt;...\"</span> to include in what will be committed<span style=\"color:#f92672\">)</span>\n" +
                " </span></span><span style=\"display:flex;\"><span>\tconfig/config.txt\n" +
                " </span></span><span style=\"display:flex;\"><span>\n" +
                " </span></span><span style=\"display:flex;\"><span>nothing added to commit but untracked files present <span style=\"color:#f92672\">(</span>use <span style=\"color:#e6db74\">\"git add\"</span> to track<span style=\"color:#f92672\">)</span>\n" +
                " </span></span></code></pre>\n" +
                "      </div>\n" +
                "      <p><code>-uall</code> нам покажет файлы в новых директориях, без этого параметра не увидим содержимое <code>config</code></p>\n" +
                "      <p>Файл на месте. Обратите внимание, в этом случае он просто скопирован в рабочую директорию и к коммиту не подготовлен и не отслеживается гитом.</p>\n" +
                "     </section>\n" +
                "     <footer class=\"article-footer\">\n" +
                "      <section class=\"article-tags\">\n" +
                "       <a href=\"/tags/git/\">git</a>\n" +
                "      </section>\n" +
                "     </footer>\n" +
                "    </article>\n" +
                "    <aside class=\"related-content--wrapper\">\n" +
                "     <h2 class=\"section-title\">Также рекомендуем</h2>\n" +
                "     <div class=\"related-content\">\n" +
                "      <div class=\"flex article-list--tile\">\n" +
                "       <article class=\"\">\n" +
                "        <a href=\"/posts/copy-repository-from-gitlab-to-github/\">\n" +
                "         <div class=\"article-details\">\n" +
                "          <h2 class=\"article-title\">Как перенести репозиторий с GitLab на Github</h2>\n" +
                "         </div></a>\n" +
                "       </article>\n" +
                "       <article class=\"\">\n" +
                "        <a href=\"/shorts/git-fetch/\">\n" +
                "         <div class=\"article-details\">\n" +
                "          <h2 class=\"article-title\">Команда git fetch</h2>\n" +
                "         </div></a>\n" +
                "       </article>\n" +
                "       <article class=\"\">\n" +
                "        <a href=\"/posts/different-git-config/\">\n" +
                "         <div class=\"article-details\">\n" +
                "          <h2 class=\"article-title\">Использование разных user/email в git репозиториях</h2>\n" +
                "         </div></a>\n" +
                "       </article>\n" +
                "       <article class=\"\">\n" +
                "        <a href=\"/posts/https-to-ssh-on-github/\">\n" +
                "         <div class=\"article-details\">\n" +
                "          <h2 class=\"article-title\">Переходим с HTTPS на SSH доступ в GitHub</h2>\n" +
                "         </div></a>\n" +
                "       </article>\n" +
                "       <article class=\"\">\n" +
                "        <a href=\"/posts/install-git-windows/\">\n" +
                "         <div class=\"article-details\">\n" +
                "          <h2 class=\"article-title\">Установка git в Windows</h2>\n" +
                "         </div></a>\n" +
                "       </article>\n" +
                "      </div>\n" +
                "     </div>\n" +
                "    </aside>\n" +
                "    <footer class=\"site-footer\">\n" +
                "     <section class=\"copyright\">\n" +
                "      © 2024 Konstantin Shibkov\n" +
                "     </section>\n" +
                "     <section class=\"powerby\">\n" +
                "      Создано при помощи <a href=\"https://gohugo.io/\" target=\"_blank\" rel=\"noopener\">Hugo</a>\n" +
                "      <br>\n" +
                "       Тема <b><a href=\"https://github.com/CaiJimmy/hugo-theme-stack\" target=\"_blank\" rel=\"noopener\" data-version=\"3.21.0\">Stack</a></b>, дизайн <a href=\"https://jimmycai.com\" target=\"_blank\" rel=\"noopener\">Jimmy</a>\n" +
                "     </section>\n" +
                "    </footer>\n" +
                "    <div class=\"pswp\" tabindex=\"-1\" role=\"dialog\" aria-hidden=\"true\">\n" +
                "     <div class=\"pswp__bg\"></div>\n" +
                "     <div class=\"pswp__scroll-wrap\">\n" +
                "      <div class=\"pswp__container\">\n" +
                "       <div class=\"pswp__item\"></div>\n" +
                "       <div class=\"pswp__item\"></div>\n" +
                "       <div class=\"pswp__item\"></div>\n" +
                "      </div>\n" +
                "      <div class=\"pswp__ui pswp__ui--hidden\">\n" +
                "       <div class=\"pswp__top-bar\">\n" +
                "        <div class=\"pswp__counter\"></div><button class=\"pswp__button pswp__button--close\" title=\"Close (Esc)\"></button> <button class=\"pswp__button pswp__button--share\" title=\"Share\"></button> <button class=\"pswp__button pswp__button--fs\" title=\"Toggle fullscreen\"></button> <button class=\"pswp__button pswp__button--zoom\" title=\"Zoom in/out\"></button>\n" +
                "        <div class=\"pswp__preloader\">\n" +
                "         <div class=\"pswp__preloader__icn\">\n" +
                "          <div class=\"pswp__preloader__cut\">\n" +
                "           <div class=\"pswp__preloader__donut\"></div>\n" +
                "          </div>\n" +
                "         </div>\n" +
                "        </div>\n" +
                "       </div>\n" +
                "       <div class=\"pswp__share-modal pswp__share-modal--hidden pswp__single-tap\">\n" +
                "        <div class=\"pswp__share-tooltip\"></div>\n" +
                "       </div><button class=\"pswp__button pswp__button--arrow--left\" title=\"Previous (arrow left)\"> </button> <button class=\"pswp__button pswp__button--arrow--right\" title=\"Next (arrow right)\"> </button>\n" +
                "       <div class=\"pswp__caption\">\n" +
                "        <div class=\"pswp__caption__center\"></div>\n" +
                "       </div>\n" +
                "      </div>\n" +
                "     </div>\n" +
                "    </div>\n" +
                "    <script src=\"https://cdn.jsdelivr.net/npm/photoswipe@4.1.3/dist/photoswipe.min.js\" integrity=\"sha256-ePwmChbbvXbsO02lbM3HoHbSHTHFAeChekF1xKJdleo=\" crossorigin=\"anonymous\" defer>\n" +
                "             </script>\n" +
                "    <script src=\"https://cdn.jsdelivr.net/npm/photoswipe@4.1.3/dist/photoswipe-ui-default.min.js\" integrity=\"sha256-UKkzOn/w1mBxRmLLGrSeyB4e1xbrp4xylgAWb3M42pU=\" crossorigin=\"anonymous\" defer>\n" +
                "             </script>\n" +
                "    <link rel=\"stylesheet\" href=\"https://cdn.jsdelivr.net/npm/photoswipe@4.1.3/dist/default-skin/default-skin.min.css\" crossorigin=\"anonymous\">\n" +
                "    <link rel=\"stylesheet\" href=\"https://cdn.jsdelivr.net/npm/photoswipe@4.1.3/dist/photoswipe.min.css\" crossorigin=\"anonymous\">\n" +
                "   </main>\n" +
                "  </div>\n" +
                "  <script src=\"https://cdn.jsdelivr.net/npm/node-vibrant@3.1.6/dist/vibrant.min.js\" integrity=\"sha256-awcR2jno4kI5X0zL8ex0vi2z+KMkF24hUW8WePSA9HM=\" crossorigin=\"anonymous\">\n" +
                "             </script>\n" +
                "  <script type=\"text/javascript\" src=\"/ts/main.js\" defer></script>\n" +
                "  <script>\n" +
                "     (function () {\n" +
                "         const customFont = document.createElement('link');\n" +
                "         customFont.href = \"https://fonts.googleapis.com/css2?family=Lato:wght@300;400;700&display=swap\";\n" +
                " \n" +
                "         customFont.type = \"text/css\";\n" +
                "         customFont.rel = \"stylesheet\";\n" +
                " \n" +
                "         document.head.appendChild(customFont);\n" +
                "     }());\n" +
                " </script>\n" +
                "  <script defer src=\"/fontawesome/js/all.min.js\"></script>\n" +
                "  <script type=\"text/javascript\">\n" +
                "    (function(m,e,t,r,i,k,a){m[i]=m[i]||function(){(m[i].a=m[i].a||[]).push(arguments)};\n" +
                "    m[i].l=1*new Date();k=e.createElement(t),a=e.getElementsByTagName(t)[0],k.async=1,k.src=r,a.parentNode.insertBefore(k,a)})\n" +
                "    (window, document, \"script\", \"https://mc.yandex.ru/metrika/tag.js\", \"ym\");\n" +
                " \n" +
                "    ym(10967989, \"init\", {\n" +
                "         clickmap:true,\n" +
                "         trackLinks:true,\n" +
                "         accurateTrackBounce:true\n" +
                "    });\n" +
                " </script>\n" +
                "  <noscript>\n" +
                "   <div>\n" +
                "    <img src=\"https://mc.yandex.ru/watch/10967989\" style=\"position:absolute; left:-9999px;\" alt=\"\">\n" +
                "   </div>\n" +
                "  </noscript>\n" +
                " </body>");
        System.out.println(withoutTags);
        ConcurrentHashMap<String, Integer> lemmas = lemmaFinder.findAllLemmas(withoutTags);
        for (Map.Entry<String, Integer> entry : lemmas.entrySet()) {
            System.out.println(entry);
        }

//        String validate = "/ghguhreuhfue.PNg";
//
//        System.out.println(validate.matches("(?i)/.+[.]((png)|(jpg)|(jpeg))"));



    }
}
