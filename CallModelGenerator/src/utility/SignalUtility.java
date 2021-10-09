package utility;

import java.util.List;

import model.Signal;

public class SignalUtility {

	public String parseSignalName(String str) {
		for (String type : Signal.reqandResp) {
			if (str.contains(type)) {
				return type;
			}
		}
		return null;
	}

	public String parseTypeofSignal(String str) {
		if (str.contains(Signal.REQUEST)) {
			return Signal.REQUEST;
		} else if (str.contains(Signal.RESPONSE)) {
			return Signal.RESPONSE;
		} else if (str.contains(Signal.REQ)) {
			return Signal.REQUEST;
		} else if (str.contains(Signal.RESP)) {
			return Signal.RESPONSE;
		}
		return null;
	}

	/**
	 * Sorts the signals according to line number.
	 * */
	public List<Signal> sortSignals(List<Signal> list) {
		heapSort(list);
		return list;
	}

	private void heapSort(List<Signal> list) {
		int size = list.size();

		// Build heap
		for (int i = size / 2 - 1; i >= 0; i--)
			heapify(list, size, i);

		// One by one extract (Max) an element from heap and
		// replace it with the last element in the array
		for (int i = size - 1; i >= 0; i--) {

			Signal temp = list.get(0);
			list.set(0, list.get(i));
			list.set(i, temp);

			// call max heapify on the reduced heap
			heapify(list, i, 0);
		}
	}

	// To heapify a subtree with node i
	private void heapify(List<Signal> list, int heapSize, int i) {
		int largest = i; // Initialize largest as root
		int leftChildIdx = 2 * i + 1; // left = 2*i + 1
		int rightChildIdx = 2 * i + 2; // right = 2*i + 2

		// If left child is larger than root
		if (leftChildIdx < heapSize && list.get(leftChildIdx).getLine() > list.get(largest).getLine())
			largest = leftChildIdx;

		// If right child is larger than largest so far
		if (rightChildIdx < heapSize && list.get(rightChildIdx).getLine() > list.get(largest).getLine())
			largest = rightChildIdx;

		// If largest is not root
		if (largest != i) {
			Signal swap = list.get(i);
			list.set(i, list.get(largest));
			list.set(largest, swap);

			// Recursive call to heapify the sub-tree
			heapify(list, heapSize, largest);
		}
	}
}
