/**
 * Created by plter on 6/30/17.
 */

(function () {

    var questions = [];
    var questionsJsonOutput;
    var currentTimerId = -1;

    function buildUI() {
        $('body').append(
            "<div style='position: absolute;top: 500px;z-index: 10000'>" +
            "   <div>" +
            "       <button class='btn-start-get-info'>Start to get question info</button>" +
            "       <button class='btn-print-data' style='margin-left: 20px;'>Print data</button>" +
            "   </div>" +
            "   <textarea class='questions-json-output' style='z-index: 10000;'></textarea>" +
            "</div>");

        questionsJsonOutput = $(".questions-json-output");
        questionsJsonOutput.hide();
    }

    function retrieveCorrectAnswerTag(answer) {
        return answer.substring(0, answer.indexOf(".")).trim();
    }

    function retrieveQuestionInfo() {
        var titleSource = $(".shiti-content").html();
        var title = titleSource.substr(titleSource.indexOf(".") + 1).trim();

        var optionsContainer = $(".options-container");
        var options = optionsContainer.find("span");
        var correctAnswers = optionsContainer.find(".dui span");
        var type = "Unknown";
        var a, b, c, d;
        a = options[0].innerHTML;
        b = options[1].innerHTML;
        switch (options.length) {
            case 4:
                if (correctAnswers.length <= 1) {
                    type = "single";
                } else {
                    type = "multi";
                }
                c = options[2].innerHTML;
                d = options[3].innerHTML;
                break;
            case 2:
                type = 'judge';
                break;
        }
        var answer = "";
        correctAnswers.each(function () {
            answer += retrieveCorrectAnswerTag(this.innerHTML);
        });

        var description = $(".explain-container *[data-item='explain-content']").html();
        if (description) {
            description = description.trim();
        }
        var image = $(".media-container img").attr("src");
        var video = $(".media-container video").attr("src");

        var question = {
            type: type,
            title: title,
            a: a,
            b: b,
            c: c,
            d: d,
            answer: answer,
            image: image,
            video: video,
            description: description
        };

        questions.push(question);
        console.log("Retrieve question " + question.title);

        var tag = titleSource.substring(0, titleSource.indexOf("."));
        var tokens = tag.split("/");
        if (tokens[0].trim() === tokens[1].trim()) {
            if (currentTimerId !== -1) {
                clearInterval(currentTimerId);
                alert("完成");
            }
        }
    }

    function addListeners() {
        var nextBtn = $(".shiti-buttons button[data-item='next']");

        $(".btn-start-get-info").click(function () {
            retrieveQuestionInfo();

            if (currentTimerId === -1) {
                currentTimerId = setInterval(function () {
                    nextBtn.click();
                    setTimeout(retrieveQuestionInfo, 400);
                }, 500);
            }
        });
        $(".btn-print-data").click(function () {
            questionsJsonOutput.show();
            questionsJsonOutput.val(JSON.stringify({questions: questions}));
        });
    }

    function init() {
        buildUI();
        addListeners();
    }

    init();
})();
