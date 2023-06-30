#-*-encoding=utf8-*-

import codecs
import argparse
import torch
from transformers import BertTokenizer

def getparser():
    parser = argparse.ArgumentParser()
    parser.add_argument("--trainpref",type=str,required=True)
    parser.add_argument("--train2pref",type=str,required=True)
    parser.add_argument("--bert_src",type=str)
    parser.add_argument("--bert_tgt",type=str)
    parser.add_argument("--dic_src",type=str)
    parser.add_argument("--dic_tgt",type=str)
    parser.add_argument("--s",type=str,required=True)
    parser.add_argument("--t",type=str,required=True)
    parser.add_argument("--dic2_tgt",type=str)
    parser.add_argument("--dic2_src",type=str)
    parser.add_argument("--all",action="store_true")
    return parser

def main(args):
    trainpref = args.trainpref
    train2pref = args.train2pref
    filesrc = trainpref + "." + args.s
    filetgt = trainpref + "." + args.t

    filesrcsave = train2pref + "." + args.s
    filetgtsave = train2pref + "." + args.t

    bertsrc = BertTokenizer.from_pretrained(args.bert_src)
    berttgt = BertTokenizer.from_pretrained(args.bert_tgt)

    wordpiece_tokenizer = WordpieceTokenizer(vocab=bertsrc.vocab, unk_token=bertsrc.unk_token)
    bertsrc.wordpiece_tokenizer = wordpiece_tokenizer

    wordpiece_tokenizer = WordpieceTokenizer(vocab=berttgt.vocab, unk_token=berttgt.unk_token)
    berttgt.wordpiece_tokenizer = wordpiece_tokenizer

    dicsrc = args.dic_src
    dictgt = args.dic_tgt

    dicsrcsave = args.dic2_src
    dictgtsave = args.dic2_tgt

    fstreamsrc = codecs.open(filesrcsave, "w", "utf8")
    with codecs.open(filesrc, "r", "utf8") as f1:
        for line in f1.readlines():
            lines = bertsrc.tokenize(line.strip("\n").strip())
            fstreamsrc.write(" ".join(lines) + "\n")
    fstreamsrc.flush()
    fstreamsrc.close()

    fstreamtgt = codecs.open(filetgtsave, "w" , "utf8")
    with codecs.open(filetgt, "r", "utf8") as f2:
        for line in f2.readlines():
            lines = berttgt.tokenize(line.strip("\n").strip())
            fstreamtgt.write(" ".join(lines) + "\n")
    fstreamtgt.flush()
    fstreamtgt.close()

    with codecs.open(dictgtsave, "w","utf8")as f1:
        for j in range(berttgt.vocab_size):
            f1.write(berttgt.ids_to_tokens[j]+"\n")
        f1.flush()

    with codecs.open(dicsrcsave, "w","utf8") as f1:
        for j in range(bertsrc.vocab_size):
            f1.write(bertsrc.ids_to_tokens[j]+"\n")
        f1.flush()

    return

def whitespace_tokenize(text):
    """Runs basic whitespace cleaning and splitting on a piece of text."""
    text = text.strip()
    if not text:
        return []
    tokens = text.split()
    return tokens

class WordpieceTokenizer(object):
    """Runs WordPiece tokenization."""

    def __init__(self, vocab, unk_token, max_input_chars_per_word=100):
        self.vocab = vocab
        self.unk_token = unk_token
        self.max_input_chars_per_word = max_input_chars_per_word

    def tokenize(self, text):

        output_tokens = []
        for token in whitespace_tokenize(text):
            chars = list(token)
            if len(chars) > self.max_input_chars_per_word:
                output_tokens.append(token)
                continue

            is_bad = False
            start = 0
            sub_tokens = []
            while start < len(chars):
                end = len(chars)
                cur_substr = None
                while start < end:
                    substr = "".join(chars[start:end])
                    if start > 0:
                        substr = "##" + substr
                    if substr in self.vocab:
                        cur_substr = substr
                        break
                    end -= 1
                if cur_substr is None:
                    is_bad = True
                    break
                sub_tokens.append(cur_substr)
                start = end

            if is_bad:
                output_tokens.append(token)
            else:
                output_tokens.extend(sub_tokens)
        return output_tokens




def cli_main():
    parser = getparser()
    args = parser.parse_args()
    main(args)

if __name__ == "__main__":
    cli_main()

