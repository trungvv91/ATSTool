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
	- /temp: File sinh ra từ pha Tokenize, Postag, Chunker và các file để chạy VietChunker
	- /data: Folder file từ StopWords, Synomym...
		- idf_final.txt: idf của một từ trong corpus đầy đủ
		- idf_index_test.txt: idf của corpus nhỏ
	- /src: file mã nguồn
	- /yworks-uml-doclet-3.0_02-jdk1.5: thư viện sinh javadoc

