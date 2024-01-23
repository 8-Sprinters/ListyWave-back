init:
	git config core.hooksPath .githooks
	chmod +x .githooks/commit-msg
	git update-index --add --chmod=+x .githooks/commit-msg
