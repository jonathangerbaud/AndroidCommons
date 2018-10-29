package fr.jonathangerbaud.ktx

inline fun Boolean.cond(trueBlock: () -> Unit, falseBlock: () -> Unit)
{
    if (this) trueBlock() else falseBlock()
}