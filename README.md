ATSTool
=======

Chương trình Abstractive Text Summarization:
1. Thư mục chương trình
	- /cmd: chứa các chương trình command line cho SentenceDectection, Tokenization, Tagger, Chunker và Reduction (CRF++).
	- /corpus: Chứa tập văn bản thực nghiệm
		- corpus/AutoSummary: Thư mục văn bản tóm tắt tự động
		- corpus/Plaintext: Thư mục văn bản gốc
		- corpus/Summary: Thư mục văn bản tóm tắt bằng tay
		***Filename của 1 văn bản tương ứng: Văn bản gốc, Bằng máy, Bằng tay là giống nhau
	- /temp: Chứa các file sinh ra từ pha Tokenize, Postag, Chunker và các file để chạy VietChunker
	- /data: chứa file metadata từ StopWords, Synomym...
		- idf_final.txt: idf của một từ trong corpus đầy đủ
		- idf_index_test.txt: idf của corpus nhỏ
	- /src: file mã nguồn

2. Source Code:
Package:
	- nlp.default:
		- - Main.java: File chạy chương trình. Input: /corpus/Plaintext. Output: /corpus/AutoSummary
	- nlp.decompose
		- Decomposer.java: bộ tách và tạo dữ liệu train cho reduction
		- TrainData.java: model training
	- nlp.extradata :
		- Conjunction.java: Các conjunction dùng cho bước cắt tỉa theo cấu trúc diễn ngôn
		- Ts_IsfScore.java: Đọc độ đo idf từ file idf_index_test.txt hoặc idf_final.txt
		- NounAnophoric: Luật Phân giải đồng tham chiếu
		- Punction.java: Dấu câu
		- StopWords.java: Mảng Stop-Words
		- Synonym.java: Đọc danh sách từ đồng nghĩa
	- nlp.textprocess: 
		- MyToken.java: model cho 1 token (1 từ)
		- MySentence.java: model cho 1 câu
		- MyTokenizer.java: chạy thư viện tách, tag từ...
		- MyExtracter.java: extraction
		- MyReduction.java: reduction
		- WordGraphs.java: Chương trình chính chạy tóm tắt
	- nlp.evaluation : 
		- Summarization.java: Chương trình điều khiển tóm tắt
		- SumUI.java: Giao diện tóm tắt
		- Evaluation.java: Đánh giá theo ROUGE-N
		- EvalUI.java: Giao diện đánh giá
	- nlp.util:
		- CmdUtil.java: chạy command line từ Windows
		- IOUtil.java: đọc/ghi file
		- MyStringUtil.java: xử lý string
		
	
		