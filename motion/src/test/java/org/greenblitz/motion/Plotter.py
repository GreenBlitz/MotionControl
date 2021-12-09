import sys
import matplotlib.pyplot as plt


NUM_OF_POINTS = 1000
INTERVAL = 1.0 / NUM_OF_POINTS
POINTS = [i * INTERVAL for i in range(NUM_OF_POINTS+1)]


def calcPoints(coeffs):
    calcValue = lambda x: sum([coeffs[i] * (x ** i) for i in range(len(coeffs))])
    return [calcValue(point) for point in POINTS]


def main():
    for arg in sys.argv[2:]:
        # parsing the argument string to get the functions
        funcXY = arg.split('|')
        funcX = [float(coefficient) for coefficient in funcXY[0].split(',')]
        funcY = [float(coefficient) for coefficient in funcXY[1].split(',')]

        # plotting the functions
        plt.plot(calcPoints(funcX), calcPoints(funcY))

    plt.title(sys.argv[1])
    plt.xlabel('made by guy wolf the king')
    plt.ylabel('hella cool')
    plt.show()


if __name__ == '__main__':
    main()
