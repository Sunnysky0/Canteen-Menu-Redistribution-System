
def replaceCharInStr(stra, old, new):
    strf = ""
    for i in range(str(stra).__len__()):
        if stra[i] == old:
            strf += new
        else:
            strf += stra[i]

    return strf


def append(target, path):
    f = open("../main/resources/assets/food_data_s1.fson", "at")
    f.write("\ntarget:" + target + ";" + "path:" + path)
    print("Data added")


print("Press Q to exit")
while True:
    t = input("Target: ")
    if t == "q" or t == "Q":
        break

    p = input("Path:")
    if p == "q" or p == "Q":
        break

    p = replaceCharInStr(p, "，", ",")

    append(t, p)


print("Program terminated")
