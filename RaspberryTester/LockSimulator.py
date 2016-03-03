import RPi.GPIO as GPIO
import time


def setup():
    # To ignore annoying warnings
    GPIO.setwarnings(False)
    GPIO.setmode(GPIO.BCM)

    # Sets up button to PIN 18. This will act as a simulation for the car lock.
    # If the button is pressed the car is locked.
    GPIO.setup(18, GPIO.IN, pull_up_down=GPIO.PUD_UP)

    # Sets up the LED to PIN 17. This will give an indication to whether the button is pressed/car is locked.
    # If the car is unlocked the LED will be on.
    GPIO.setup(17, GPIO.OUT)
    GPIO.output(17, GPIO.LOW)


def main():
    # Checks if the car initially is locked or not.
    input_state = GPIO.input(18)

    # Send the initial state to the app. (Here simulated by printing to the consol.)
    if input_state:
        print('Car is UNLOCKED!')
        # Sets the LED as HIGH since the car is unlocked
        GPIO.output(17, GPIO.HIGH)
    else:
        print('Car is LOCKED.')
        GPIO.output(17, GPIO.LOW)

    # Runs a loop to contiously check if the input_state has changed.
    while True:
        new_input_state = GPIO.input(18)

        # Checks if the input_state has changed.
        if new_input_state != input_state:
            print('State has changed.')
            if new_input_state:
                print('Car is UNLOCKED.')
                GPIO.output(17, GPIO.HIGH)
            else:
                print('Car is LOCKED.')
                GPIO.output(17, GPIO.LOW)
        time.sleep(0.2)
        input_state = new_input_state
setup()
main()
